package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import controllers.src.JSONManager;
import filters.UserTokenFilter;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;
import utils.MessageUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class BaseController extends Controller {

    @Inject
    FormFactory formFactory;
    List<BaseModel> modelList = new ArrayList<>();
    BaseModel auxModel = null;

    final String langHeader = "Accept-Language";
    final String XML_SCHEMA = "application/xml";
    final String JSON_SCHEMA = "application/json";
    FilterProvider filters = new SimpleFilterProvider().addFilter("userTokenFilter", new UserTokenFilter());
    private final MessagesApi messagesApi;
    String lang;

    public BaseController(){
        this.messagesApi=null;
    }
    @Inject
    public BaseController(MessagesApi messagesApi){
        this.messagesApi=messagesApi;
    }

    public Result contentNegotiation(Http.Request request, BaseController baseController,Boolean delete){
        Result res;
        if (request.accepts(XML_SCHEMA)){
            Content content;
            if (modelList != null && modelList.size() > 0) {
                content = baseController.getContentXML(modelList);
            }else if (delete){
                content = views.xml.Generic.generic.render(true,getMessage(MessageUtils.deleteOk));
            }else {
                content = views.xml.Generic.generic.render(true,getMessage(MessageUtils.noResults));
            }
            res = Results.ok(content);
        }else if (request.accepts(JSON_SCHEMA)) {
            //https://grokonez.com/json/resolve-json-infinite-recursion-problems-working-jackson
            try {
                if (modelList != null && modelList.size() > 0) {
                    res = Results.ok(Json.parse(JSONManager.getResultJson(filters,modelList)));
                }else if (delete){
                    res = JSONManager.genericJsonResponse(true, getMessage(MessageUtils.deleteOk), 200);
                }else{
                    res = JSONManager.genericJsonResponse(true, getMessage(MessageUtils.noResults), 200);
                }
            } catch (JsonProcessingException e) {
                System.err.println("Error: " + e.getMessage());
                res = contentNegotiationError(request,getMessage(MessageUtils.internalError),500);
            }

        }else{
            res = contentNegotiationError(request,getMessage(MessageUtils.unsupportedError),400);
        }

        return res;
    }

    public Result contentNegotiationError(Http.Request request, String errorMsg, Integer status){
        Result res;

        if (request.accepts(XML_SCHEMA)){
            Content content = views.xml.Generic.generic.render(false,errorMsg);
            res = Results.status(status,content);
        }else if (request.accepts(JSON_SCHEMA)) {
            res = JSONManager.genericJsonResponse(false, errorMsg, status);
        }else{
            res = Results.status(status,errorMsg);
        }

        return res;
    }

    public Result checkFormErrors(Http.Request request,Form<? extends BaseModel> form){
        if (form==null)
            return contentNegotiationError(request,getMessage(MessageUtils.notFound),404);

        if (form.hasErrors()){
            String str = JSONManager.formatJsonError(form.errorsAsJson());
            return contentNegotiationError(request,str,400);
        }
        return null;
    }

    public Result getModel(Http.Request request, BaseController bc, List<? extends BaseModel> list){
        Result res = null;
        if (list != null && list.size() > 0)
            modelList.addAll(list);
        if (list != null )
            res = contentNegotiation(request,bc,false);

        if (res == null)
            res = contentNegotiationError(request,getMessage(MessageUtils.formatError),400);

        return res;
    }

    public Result getModelId(Http.Request request, BaseController baseController, BaseModel baseModel){
        Result res = null;
        if (baseModel != null) {
            modelList.add(baseModel);
            res = contentNegotiation(request,baseController,false);
        }

        if (res == null)
            res = contentNegotiationError(request,getMessage(MessageUtils.notFound),404);

        return res;
    }

    public Result saveModelResult(Http.Request request, BaseController baseController, Object modelType, int count, boolean update) {
        Result res = null;
        if (count == 0) {
            if (modelType!= null) {
                if (update && updateModel(modelType, count)) {
                    res = contentNegotiation(request, baseController, false);
                } else if (!update && saveModel(modelType, count)) {
                    res = contentNegotiation(request, baseController, false);
                }
            }

            if (res == null)
                res = contentNegotiationError(request, getMessage(MessageUtils.formatError), 400);
        }else{
            res = contentNegotiationError(request, getMessage(MessageUtils.duplicatedError), 406);
        }
        return res;
    }

    public boolean saveModel(Object modelType, int count) {
        if (modelType!=null && count == 0){
            BaseModel bm = (BaseModel) modelType;
            modelList.add(bm);
            bm.save();
            return true;
        }
        return false;
    }

    public boolean updateModel(Object modelType, int count){
        if (modelType!=null && count == 0) {
            BaseModel modelUpdate = (BaseModel) modelType;
            modelList.add(modelUpdate);
            modelUpdate.update();
            return true;
        }
        return false;
    }

    public Result deleteModelResult(Http.Request request,BaseController baseController,Object modelType){
        Result res = null;
        if (modelType!=null) {
            if (deleteModel(modelType))
                res = contentNegotiation(request, baseController,true);

            if (res == null)
                res = contentNegotiationError(request, getMessage(MessageUtils.deleteIngredientError), 400);

        }
        if (res == null)
            res = contentNegotiationError(request, getMessage(MessageUtils.notFound), 404);

        return res;
    }

    public boolean deleteModel(Object modelType){
        try {
            BaseModel modelDelete = (BaseModel) modelType;
            if (modelType != null) {
                modelDelete.delete();
                return true;
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return false;
    }

    public Long checkUserId(User u, String id, Integer type){
        if (u == null || id== null || type==null) return -1L;

        Long res = checkSelfId(u,id,type);
        if (res != -1) return res;
        try {
            res = Long.valueOf(id);
        }catch (NumberFormatException e){
            System.err.println("Error: " + e.getMessage());
        }

        return res;
    }

    public Long checkSelfId(User u, String id, Integer type){
        String self = MessageUtils.self;
        if (u!=null && id !=null && type!=null)  {
            if (type == 1) {
                if (self.equals(id) || id.equals(Long.toString(u.getRecipeBook().getId()))) {
                    return u.getRecipeBook().getId();
                }
            } else {
                if (self.equals(id) || id.equals(Long.toString(u.getId()))) {
                    return u.getId();
                }
            }
        }
        return -1L;
    }

    public BaseModel getFormModel(Form<? extends BaseModel> form){

        if (form.value().isPresent())
        return form.get();

        return auxModel;

    }

    public void initRequest(Http.Request request){
        String defaultLang = Lang.defaultLang().language();
        lang = request.header(langHeader).orElse(defaultLang);
        modelList.clear();
    }

    public String getMessage(String message){
        assert this.messagesApi != null;
        return this.messagesApi.get(Lang.apply(lang),message);
    }

    public Content getContentXML(List<BaseModel> modelList){
        return null;
    }
}
