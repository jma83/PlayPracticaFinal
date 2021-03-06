package controllers;

import actionCompostionAuth.Attrs;
import actionCompostionAuth.UserArg;
import actionCompostionAuth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.src.XMLManager;
import models.BaseModel;
import models.User;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.i18n.MessagesApi;
import play.mvc.*;
import play.twirl.api.Content;
import utils.MessageUtils;

import javax.inject.Inject;
import java.util.*;

public class UserController extends BaseController {
    String headerCount = "X-User-Count";
    public UserController() {
        super();
    }
    @Inject
    public UserController(MessagesApi messagesApi){
        super(messagesApi);
    }
    public Result createUser(Http.Request request){
        initRequest(request);
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);
        if (res == null) {
            User u = (User) getFormModel(form);
            if (u!=null) {
                u.init();
                int count = User.findUsername(u.getUsername()).size();
                if (saveModel(u, count)) {
                    u.getUserToken().setVisible(true);
                    res = contentNegotiation(request, this, false);
                }else {
                    res = contentNegotiationError(request, getMessage(MessageUtils.duplicatedError), 406);
                }
            }
        }
        if (res == null)
            res = contentNegotiationError(request, getMessage(MessageUtils.formatError), 400);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getUser(Http.Request request){
        initRequest(request);

        Result res = this.getModel(request,this,User.findAll());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result getUserId(Http.Request request, String id){
        initRequest(request);

        User userRequest = request.attrs().get(Attrs.USER);
        User u = User.findById(checkUserId(userRequest,id,0));
        Result res = getModelId(request,this,u);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result updateUser(Http.Request request, String id){
        initRequest(request);
        Form<User> form = formFactory.form(User.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            User userRequest = request.attrs().get(Attrs.USER);
            User userUpdate = User.findById(checkUserId(userRequest, id, 0));
            if (userUpdate != null) {
                if (userRequest.getId().equals(userUpdate.getId())) {
                    userUpdate.update((User) getFormModel(form));
                    int count = User.findUsernameId(userUpdate.getUsername(), userUpdate.getId()).size();
                    res = saveModelResult(request, this, userUpdate, count, true);
                }
                if (res == null)
                    res = contentNegotiationError(request, getMessage(MessageUtils.forbiddenError), 403);
            }
            if (res == null)
                res = contentNegotiationError(request, getMessage(MessageUtils.notFound), 404);

        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result deleteUser(Http.Request request, String id){
        initRequest(request);
        Result res = null;
        User userRequest = request.attrs().get(Attrs.USER);
        User usuFinal = User.findById(checkUserId(userRequest,id,0));

        if (usuFinal != null) {
            if (userRequest.getId().equals(usuFinal.getId()))
                res = deleteModelResult(request, this, usuFinal);

            if (res == null)
                res = contentNegotiationError(request,getMessage(MessageUtils.forbiddenError),403);
        }

        if (res == null)
            res = contentNegotiationError(request, getMessage(MessageUtils.notFound), 404);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Form<User> validateRequestForm(Http.Request request, Form<User> form){
        User user = new User();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(user.getTitleXML());
            User u = (User) XMLManager.createWithXML(modelNode,user).get(0);
            if (u!=null)
            form.fill(u);
            auxModel = u;
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }

        return form;
    }


    public Content getContentXML(List<BaseModel> modelList){
        User[] array = new User[modelList.size()];
        modelList.toArray(array);
        return views.xml.User.users.render(Arrays.asList(array));
    }
}


