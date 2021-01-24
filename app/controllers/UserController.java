package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import java.util.*;

public class UserController extends BaseController {

    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-User-Count";

    public Result createUser(Http.Request request){
        Result res = null;
        Form<User> form = null;
        User user = new User();

        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(user.getTitleXML());
            User u = (User) createWithXML(modelNode,user).get(0);
            form = formFactory.form(User.class).fill(u);
        }else if (json != null){
            form = formFactory.form(User.class).bindFromRequest(request);
        }else{
            res = Results.badRequest(noResults);
        }

        user = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        user.save();
        modelList.add(user);
        System.out.println("User inserted: " + user);


        if (res==null)
            res = this.contentNegotiation(request,getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result getUser(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        modelList.addAll(User.findAll());
        if (modelList.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexUser(index.get());

        if (res == null)
            res = this.contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexUser(String index){
        Result res = null;

        System.out.println(index);
        modelList.clear();
        try {
            User u = User.findById(Integer.parseInt(index));
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
        Form<User> form = formFactory.form(User.class).bindFromRequest(request);
        Optional<String> index = request.queryString("index");
        User user = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        if (user != null && res == null && index.isPresent()){
            Long id = Long.valueOf(index.get());
            User userUpdate = User.findById(id);
            userUpdate.update(user);
            modelList.add(userUpdate);
            userUpdate.update();
            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result deleteUser(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            User usuFinal = User.findById(id);
            this.modelList.add(usuFinal);
            usuFinal.delete();

            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Content getContentXML(){
        User[] array = new User[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.User.users.render(Arrays.asList(array));
        return content;
    }
}
