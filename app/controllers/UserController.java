package controllers;

import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.BaseModel;
import models.User;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.*;
import play.twirl.api.Content;

import java.util.*;

public class UserController extends BaseController {


    String headerCount = "X-User-Count";

    @Security.Authenticated(UserAuthenticator.class)
    public Result createUser(Http.Request request){
        return postUser(request,true);
    }

    public Result loginUser(Http.Request request){
        return postUser(request,false);
    }

    public Result postUser(Http.Request request, boolean saveModel){

        Form<User> form = formFactory.form(User.class);
        Result res = null;
        form = validateRequestForm(request,form);
        if (form != null) {
            res = checkFormErrors(form);
            if (res == null) {
                this.saveModel(form.get(), saveModel);
                res = this.contentNegotiation(request, getContentXML());
            }
        }else{
            res = Results.badRequest(noResults);
        }


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result getUser(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        modelList.addAll(User.findAll());
        if (modelList.size() == 0)
            res = Results.notFound(noResults);

        if (res == null && index.isPresent())
            res = this.getIndexUser(index.get());

        if (res == null)
            res = this.contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexUser(String index){
        Result res = null;

        try {
            modelList.clear();
            User u = User.findById(Long.valueOf(index));
            if (u != null) {
                modelList.add(u);
            }else{
                res = Results.notFound(noResults);
            }
        } catch (NumberFormatException e) {
            res = Results.badRequest(formatError);
        }

        return res;
    }

    public Result updateUser(Http.Request request){
        Result res = null;
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);
        if (form != null) {
            Optional<String> index = request.queryString("index");

            res = checkFormErrors(form);
            if (res == null && index.isPresent()) {
                Long id = Long.valueOf(index.get());
                User userUpdate = User.findById(id);
                userUpdate.update(form.get());
                this.updateModel(userUpdate);
                res = this.contentNegotiation(request, getContentXML());
            }
        }else{
            res = Results.badRequest(noResults);
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result deleteUser(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            User usuFinal = User.findById(id);
            this.deleteModel(usuFinal);

            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Form<User> validateRequestForm(Http.Request request, Form<User> form){
        User user = new User();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(user.getTitleXML());
            User u = (User) createWithXML(modelNode,user).get(0);
            form.fill(u);
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }


        return form;
    }

    public Result checkFormErrors(Form<User> form){
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            System.err.println(form.errors());
            return Results.badRequest(form.errorsAsJson());
        }
        return null;
    }

    public Content getContentXML(){
        User[] array = new User[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.User.users.render(Arrays.asList(array));
        return content;
    }
}


