package controllers;

import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.User;
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
            if (!saveModel(u, count)){
                res = contentNegotiationError(request,duplicatedError,406);
            }
        }

        if (res==null)
            res = contentNegotiation(request, getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getUser(Http.Request request){
        clearModelList();
        Result res = null;

        modelList.addAll(User.findAll());
        if (modelList.size() == 0)
            res = contentNegotiationError(request,noResults,404);

        if (res == null)
            res = contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getUserId(Http.Request request, Long id){
        clearModelList();
        Result res = null;

        try {
            User u = User.findById(id);
            if (u != null) {
                modelList.add(u);
                res = contentNegotiation(request,getContentXML());
            }else{
                res = contentNegotiationError(request,noResults,404);
            }
        } catch (Exception e) {
            res = contentNegotiationError(request,formatError,400);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result updateUser(Http.Request request, Long id){
        clearModelList();
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);

        Result res = checkFormErrors(request,form);
        if (res == null && id != null && id > 0) {
            User userUpdate = User.findById(id);
            userUpdate.update(form.get());
            if (!updateModel(userUpdate))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request, getContentXML());
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteUser(Http.Request request, Long id){
        clearModelList();
        Result res = null;
        if (id != null && id > 0){
            User usuFinal = User.findById(id);
            /*UserToken userToken = usuFinal.getUserToken();
            RecipeBook recipeBook = usuFinal.getRecipeBook();
            deleteModel(userToken,false);
            deleteModel(recipeBook,false);*/

            if (!deleteModel(usuFinal,true))
                res = contentNegotiationError(request,noResults,404);

        }else {
            res = contentNegotiationError(request, missingId, 400);
        }
        if(res == null){
            res = contentNegotiation(request,getContentXML());
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


    public Content getContentXML(){
        User[] array = new User[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.User.users.render(Arrays.asList(array));
        return content;
    }
}


