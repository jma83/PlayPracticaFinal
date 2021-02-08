package controllers;

import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.RecipeBook;
import models.User;
import models.UserToken;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.*;
import play.twirl.api.Content;

import java.util.*;

public class UserController extends BaseController {
    String headerCount = "X-User-Count";


    public Result createUser(Http.Request request){
        clearModelList();
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            User u = form.get();
            u.init();
            int count = User.findUsername(u.getUsername()).size();
            if (!this.saveModel(u, count)){
                res = contentNegotiationError(request,duplicatedError,406);
            }
        }

        if (res==null)
            res = this.contentNegotiation(request, getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getUser(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);

        modelList.addAll(User.findAll());
        if (modelList.size() == 0)
            res = contentNegotiationError(request,noResults,404);

        if (res == null && index.isPresent())
            res = this.getIndexUser(request,index.get());

        if (res == null)
            res = this.contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexUser(Http.Request request, String index){
        clearModelList();
        Result res = null;

        try {
            User u = User.findById(Long.valueOf(index));
            if (u != null) {
                modelList.add(u);
            }else{
                res = contentNegotiationError(request,noResults,404);
            }
        } catch (NumberFormatException e) {
            res = contentNegotiationError(request,formatError,400);
        }

        return res;
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result updateUser(Http.Request request){
        clearModelList();
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);
        Optional<String> index = request.queryString(idQuery);

        Result res = checkFormErrors(request,form);
        if (res == null && index.isPresent()) {
            Long id = Long.valueOf(index.get());
            User userUpdate = User.findById(id);
            userUpdate.update(form.get());
            if (!this.updateModel(userUpdate)) res = contentNegotiationError(request,noResults,404);
            res = this.contentNegotiation(request, getContentXML());
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteUser(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            User usuFinal = User.findById(id);
            /*UserToken userToken = usuFinal.getUserToken();
            RecipeBook recipeBook = usuFinal.getRecipeBook();
            this.deleteModel(userToken,false);
            this.deleteModel(recipeBook,false);*/

            if (!this.deleteModel(usuFinal,true))
                res = this.contentNegotiationError(request,noResults,404);
            else
                res = this.contentNegotiation(request,getContentXML());



        }else {
            res = contentNegotiationError(request, missingId, 400);
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

    public Result checkFormErrors(Http.Request request,Form<User> form){
        if (form==null)
            return contentNegotiationError(request,noResults,400);

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            System.err.println(form.errors());
            return contentNegotiationError(request,form.errors().toString(),400);
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


