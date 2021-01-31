package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import org.w3c.dom.Document;
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
    String formatError = "Error, incorrect format";

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

    public Result contentNegotiation(Http.Request request, Content content){
        Result res = null;
        System.out.println(modelList.size());
        if (request.accepts("application/xml")){

            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String result = mapper.writeValueAsString(modelList);
                res = Results.ok(Json.parse(result));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }else{
            res = Results.badRequest();
        }
        modelList.clear();

        return res;
    }

    public void saveModel(Object modelType, boolean save){
            BaseModel bm = (BaseModel) modelType;
            if (save)
                bm.save();

            modelList.add(bm);
            System.out.println("Model inserted: " + modelType);

    }

    public void updateModel(Object modelType){
        BaseModel modelUpdate = (BaseModel) modelType;
        modelList.add(modelUpdate);
        modelUpdate.update();
    }

    public void deleteModel(Object modelType){
        BaseModel modelDelete = (BaseModel) modelType;
        this.modelList.add(modelDelete);
        modelDelete.delete();
    }


    public String getTextNode(Element e) {
        return e.getChildNodes().item(0).getNodeValue();
    }


}
