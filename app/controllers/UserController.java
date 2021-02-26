package controllers;

import auth.Attrs;
import auth.PassArgAction;
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

    public Result createUser(Http.Request request){ //OK
        clearModelList();
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            User u = form.get();
            u.init();
            int count = User.findUsername(u.getUsername()).size();
            if (saveModel(u, count)){
                u.getUserToken().setVisible(true);
                res = contentNegotiation(request, this);
            }
        }

        if (res == null)
            res = contentNegotiationError(request,duplicatedError,406);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getUser(Http.Request request){    //OK
        clearModelList();

        modelList.addAll(User.findAll());
        Result res = this.getModel(request,this);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result getUserId(Http.Request request, String id){   //OK
        clearModelList();
        Result res = null;

        User userRequest = request.attrs().get(Attrs.USER);
        User u = User.findById(checkUserId(userRequest,id,0));
        if (u != null) {
            modelList.add(u);
            res = contentNegotiation(request,this);
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result updateUser(Http.Request request, String id){  //Ok
        clearModelList();
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            User userRequest = request.attrs().get(Attrs.USER);
            User userUpdate = User.findById(checkUserId(userRequest,id,0));
            if (userUpdate != null) {
                userUpdate.update(form.get());
                int count = User.findUsernameId(userUpdate.getUsername(), userUpdate.getId()).size();
                if (updateModel(userUpdate, count))
                    res = contentNegotiation(request, this);
            }
            if (res == null)
                res = contentNegotiationError(request,noResults,404);
        }

        if (res == null)
            res = contentNegotiationError(request,formatError,400);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result deleteUser(Http.Request request, String id){  //Ok
        clearModelList();
        User userRequest = request.attrs().get(Attrs.USER);
        User usuFinal = User.findById(checkUserId(userRequest,id,0));

        Result res = deleteModelResult(request,usuFinal);

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


