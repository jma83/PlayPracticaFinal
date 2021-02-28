package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import filters.UserTokenFilter;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;
import controllers.src.XMLManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class BaseController extends Controller {

    @Inject
    FormFactory formFactory;
    List<BaseModel> modelList = new ArrayList<>();
    String noResults = "No results!";
    final String formatError = "Error, incorrect format";
    final String forbiddenError = "Error, you don't have permission to modify this element";
    final String duplicatedError = "Error, Duplicated register";
    final String deleteIngredientError = "Error, Can't delete this Ingredient, is being used in one or more Recipes";
    final String deleteOk = "The model has been deleted successfully";
    FilterProvider filters = new SimpleFilterProvider().addFilter("userTokenFilter", new UserTokenFilter());
    XMLManager xmlManager = new XMLManager();


    public Result contentNegotiation(Http.Request request, BaseController baseController){
        Result res = null;
        if (request.accepts("application/xml")){
            if (modelList != null && modelList.size() > 0) {
                res = Results.ok(baseController.getContentXML(modelList));
            }else{
                Content content = views.xml.Generic.generic.render(true,deleteOk);
                res = Results.ok(content);
            }
        }else if (request.accepts("application/json")) {
            //https://grokonez.com/json/resolve-json-infinite-recursion-problems-working-jackson
            try {
                if (modelList != null && modelList.size() > 0) {
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writer(filters).writeValueAsString(modelList);
                    res = Results.ok(Json.parse(result));
                }else{
                    res = genericJsonResponse(true, deleteOk, 200);
                }
            } catch (JsonProcessingException e) {
                System.err.println("Error: " + e.getMessage());
                contentNegotiationError(request,e.getMessage(),500);
            }

        }else{
            res = Results.badRequest("Unsupported format");
        }

        return res;
    }

    public Result contentNegotiationError(Http.Request request, String errorMsg, Integer status){
        Result res;
        boolean b = false;
        if (status == 404) b=true;

        if (request.accepts("application/xml")){
            Content content = views.xml.Generic.generic.render(b,errorMsg);
            res = Results.status(status,content);
        }else if (request.accepts("application/json")) {
            res = genericJsonResponse(b, errorMsg, status);
        }else{
            res = Results.status(status,errorMsg);
        }

        return res;

    }

    public Result checkFormErrors(Http.Request request,Form<? extends BaseModel> form){
        if (form==null)
            return contentNegotiationError(request,noResults,404);

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            System.err.println(form.errors());
            return contentNegotiationError(request,form.errors().toString(),400);
        }
        return null;
    }

    public Result getModel(Http.Request request, BaseController bc, List<? extends BaseModel> list){
        Result res = null;
        if (list != null && list.size() > 0)
            modelList.addAll(list);

        if (modelList.size() == 0)
            res = contentNegotiationError(request,noResults,404);

        if (res == null)
            res = contentNegotiation(request,bc);

        if (res == null)
            res = contentNegotiationError(request,this.formatError,400);

        return res;
    }

    public Result getModelId(Http.Request request, BaseController baseController, BaseModel baseModel){
        Result res = null;
        if (baseModel != null) {
            modelList.add(baseModel);
            res = contentNegotiation(request,baseController);
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);

        return res;
    }

    public Result saveModelResult(Http.Request request, BaseController baseController, Object modelType, int count, boolean update) {
        Result res = null;
        if (count == 0) {
            if (update && updateModel(modelType, count)){
                res = contentNegotiation(request, baseController);
            }else if (!update && saveModel(modelType, count)){
                res = contentNegotiation(request, baseController);
            }

            if (res == null)
                res = contentNegotiationError(request, formatError, 400);
        }else{
            res = contentNegotiationError(request, duplicatedError, 406);
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
                res = contentNegotiation(request, baseController);

            if (res == null)
                res = contentNegotiationError(request, deleteIngredientError, 400);

        }
        if (res == null)
            res = contentNegotiationError(request, noResults, 404);

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
        String self = "self";
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


    public Result genericJsonResponse(Boolean success, String errorMsg, Integer status){

        ObjectNode objectNode = Json.newObject();
        objectNode.put("success", success);
        objectNode.put("message", errorMsg);
        return Results.status(status,objectNode);

    }

    public Content getContentXML(List<BaseModel> modelList){
        return null;
    }
    public void clearModelList(){
        modelList.clear();
    }


}
