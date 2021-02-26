package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import filters.UserTokenFilter;
import models.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseController extends Controller {

    @Inject
    FormFactory formFactory;
    List<BaseModel> modelList = new ArrayList<>();
    String noResults = "No results!";
    final String formatError = "Error, incorrect format";
    final String missingId = "Error, missing id";
    final String duplicatedError = "Error, Duplicated register";
    final String deleteIngredientError = "Error, Can't delete this Ingredient, is being used in one or more Recipes";
    final String deleteOk = "The model has been deleted successfully";
    FilterProvider filters = new SimpleFilterProvider().addFilter("userTokenFilter", new UserTokenFilter());

    public List<BaseModel> createWithXML(NodeList modelNode,Object instance){
        List<BaseModel> models = new ArrayList<>();
        //Fuente: https://stackoverflow.com/questions/15315368/java-reflection-get-all-private-fields
        Field[] allFields = instance.getClass().getDeclaredFields();


        for (int i = 0;i <modelNode.getLength();i++){
            Element a = (Element) modelNode.item(i);
            Object instance1 = null;
            //Fuente: https://stackoverflow.com/questions/10470263/create-new-object-using-reflection
            try {
                instance1 = instance.getClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (Field field : allFields) {
                NodeList nodeList2 = a.getElementsByTagName(field.getName());
                Element e = (Element)nodeList2.item(0);
                if (e != null) {
                    String s = getTextNode(e);
                    Object obj = castObject(s, field);
                    if (!(obj instanceof BaseModel)) {
                        invokeSetter(instance1, field.getName(), obj);
                    } else {
                        BaseModel bm = (BaseModel) obj;
                        List<BaseModel> l = createWithXML(e.getElementsByTagName(bm.getTitleXML()), obj);
                        invokeSetter(instance1, field.getName(), l);
                    }
                }

            }
            if (instance1!=null)
            models.add((BaseModel) instance1);
        }

        return models;
    }



    //Fuente: https://java2blog.com/invoke-getters-setters-using-reflection-java/
    public void invokeSetter(Object obj,String propertyName, Object variableValue){
        try {
            propertyName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            Method method = obj.getClass().getDeclaredMethod("set"+propertyName,variableValue.getClass());
            try {
                method.invoke(obj,variableValue);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public Object castObject(String value, Field f) {
        Object finalObj = value;
        if (f.getType() == Integer.class){
            finalObj = Integer.parseInt(value);
        }else if (f.getType() == Date.class){
            try {
                finalObj = new SimpleDateFormat("dd/MM/yyyy").parse(value);
            } catch (ParseException e) {
                System.out.println("Error formato!");
                e.printStackTrace();
            }
        }else if (f.getType() == List.class){
            if (f.getName().equals("ingredientList")){
                finalObj = new Ingredient();
            }else if (f.getName().equals("recipeList")){
                finalObj = new Recipe();
            }else if (f.getName().equals("tagList")){
                finalObj = new Tag();
            }
        }
        return finalObj;
    }

    public Result getModel(Http.Request request, BaseController bc){
        Result res = null;
        if (modelList.size() == 0)
            res = contentNegotiationError(request,noResults,404);

        if (res == null)
            res = contentNegotiation(request,bc);

        if (res == null)
            res = contentNegotiationError(request,this.formatError,400);

        return res;
    }

    public Result contentNegotiation(Http.Request request, BaseController baseController){
        Result res = null;
        if (request.accepts("application/xml")){
            if (modelList != null && modelList.size() > 0) {
                res = Results.ok(baseController.getContentXML());
            }else{
                Content content = views.xml.Generic._generic.render(true,deleteOk);
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
                System.out.println("Error: " + e.getMessage());
                contentNegotiationError(request,e.getMessage(),500);
            }

        }else{
            res = Results.badRequest("Unsupported format");
        }

        return res;
    }

    public Result contentNegotiationError(Http.Request request, String errorMsg, Integer status){
        Result res = null;
        Boolean b = false;
        if (status == 404) b=true;

        if (request.accepts("application/xml")){
            Content content = views.xml.Generic._generic.render(b,errorMsg);
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
            return contentNegotiationError(request,noResults,400);

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            System.err.println(form.errors());
            return contentNegotiationError(request,form.errors().toString(),400);
        }
        return null;
    }


    public boolean saveModel(Object modelType, int count) {
        if (count != 0) return false;

        if (modelType!=null){
            BaseModel bm = (BaseModel) modelType;
            modelList.add(bm);
            bm.save();
            return true;
        }
        return false;
    }

    public boolean updateModel(Object modelType, int count){
        if (count != 0) return false;

        if (modelType!=null) {
            BaseModel modelUpdate = (BaseModel) modelType;
            modelList.add(modelUpdate);
            modelUpdate.update();
            return true;
        }
        return false;
    }

    public Result deleteModelResult(Http.Request request,Object modelType){
        Result res = null;
        if (deleteModel(modelType)){
            res = contentNegotiation(request, this);

            if (res == null)
                res = contentNegotiationError(request, noResults,404);
        }
        return res;
    }

    public boolean deleteModel(Object modelType){
        if (modelType!=null) {
            try {
                BaseModel modelDelete = (BaseModel) modelType;
                modelDelete.delete();
                return true;

            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
        return false;
    }

    protected Long checkUserId(User u, String id, Integer type){
        if (u == null) return -1L;

        Long res = checkSelfId(u,id,type);
        if (res != -1) return res;
        try {
            res = Long.valueOf(id);
        }catch (NumberFormatException e){
            System.out.println("Error: " + e.getMessage());
        }

        return res;
    }

    protected Long checkSelfId(User u, String id, Integer type){
        if (type == 1) {
            if ("self".equals(id) || id.equals(Long.toString(u.getRecipeBook().getId()))) {
                return u.getRecipeBook().getId();
            }
        }else{
            if ("self".equals(id) || id.equals(Long.toString(u.getId()))) {
                return u.getId();
            }
        }
        return -1L;
    }

    public void clearModelList(){
        modelList.clear();
    }

    public Result genericJsonResponse(Boolean success, String errorMsg, Integer status){

        ObjectNode objectNode = Json.newObject();
        objectNode.put("success", success);
        objectNode.put("message", errorMsg);
        Result res = Results.status(status,objectNode);

        return res;
    }

    public String getTextNode(Element e) {
        return e.getChildNodes().item(0).getNodeValue();
    }

    public Content getContentXML(){
        return null;
    }

}
