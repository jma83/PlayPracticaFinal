package controllers;

import models.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import scala.xml.Elem;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseController {

    public List<BaseModel> createWithXML(Document req,Object instance, Object controllerInstance){
        List<BaseModel> models = new ArrayList<>();
        Field[] allFields = instance.getClass().getDeclaredFields();

        BaseModel baseModel = (BaseModel)instance;
        String title = baseModel.getTitleXML();
        NodeList modelNode = req.getElementsByTagName(title);

        for (int i = 0;i <modelNode.getLength();i++){
            Element a = (Element) modelNode.item(i);
            //Fuente: https://stackoverflow.com/questions/15315368/java-reflection-get-all-private-fields
            for (Field field : allFields) {
                Element e = (Element) a.getElementsByTagName(field.getName()).item(0);
                if (e != null) {
                    String s = getTextNode(e);
                    Object obj = castObject(s,field);
                    if (obj != null) {
                        invokeSetter(instance, field.getName(), obj);
                    }else{
                        BaseController bc = (BaseController) controllerInstance;
                        bc.iterateElementList();
                    }
                }
            }
            models.add((BaseModel) instance);
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
                finalObj = new SimpleDateFormat("dd-MMM-yyyy").parse(value);
            } catch (ParseException e) {
                System.out.println("Error formato!");
                e.printStackTrace();
            }
        }else if (f.getType() == List.class){
            System.out.println("Holi!");
            finalObj = null;
        }
        return finalObj;
    }

    public void iterateElementList(){
        System.out.println("iterateElementList PARENT!");
    }



    public String getTextNode(Element e) {
        return e.getChildNodes().item(0).getNodeValue();
    }
}
