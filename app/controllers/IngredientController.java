package controllers;

import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.Ingredient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Content;

import java.util.Arrays;
import java.util.List;

public class IngredientController extends BaseController {

    String headerCount = "X-Ingredient-Count";


    public Result createIngredient(Http.Request request){
        clearModelList();
        Form<Ingredient> form = formFactory.form(Ingredient.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            Ingredient i = form.get();
            //r.init();
            List<Ingredient> list = Ingredient.findByName(i.getName());
            if (!saveModel(i, list.size())){
                res = contentNegotiationError(request,duplicatedError,406);
            }
        }

        if (res==null)
            res = contentNegotiation(request, this);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredient(Http.Request request){
        clearModelList();
        Result res = null;

        modelList.addAll(Ingredient.findAll());
        res = this.getModel(request,this);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIngredientId(Http.Request request, Long id){
        clearModelList();
        Result res = null;

        try {
            Ingredient i = Ingredient.findById(id);
            if (i != null) {
                modelList.add(i);
                res = contentNegotiation(request,this);
            }else{
                res = contentNegotiationError(request,noResults,404);
            }
        } catch (NumberFormatException e) {
            res = contentNegotiationError(request, formatError, 400);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result updateIngredient(Http.Request request, Long id){
        clearModelList();
        Form<Ingredient> form = formFactory.form(Ingredient.class);
        form = validateRequestForm(request,form);

        Result res = checkFormErrors(request,form);
        if (res == null ) {
            Ingredient ingredientUpdate = Ingredient.findById(id);
            ingredientUpdate.update(form.get());
            int count = Ingredient.findByName(ingredientUpdate.getName()).size();
            if (updateModel(ingredientUpdate,count))
                res = contentNegotiation(request, this);
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteIngredient(Http.Request request, Long id){
        clearModelList();
        Result res = null;
        if (id > 0){
            Ingredient ingrFinal = Ingredient.findById(id);

            if (!deleteModel(ingrFinal))
                res = contentNegotiationError(request,deleteIngredientError,404);
            else
                res = contentNegotiation(request,this);

        }else {
            res = contentNegotiationError(request, missingId, 400);
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Form<Ingredient> validateRequestForm(Http.Request request, Form<Ingredient> form){
        Ingredient ingredient = new Ingredient();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(ingredient.getTitleXML());
            form.fill((Ingredient) createWithXML(modelNode,ingredient).get(0));
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }

        return form;
    }

    public Content getContentXML(){
        Ingredient[] array = new Ingredient[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.Ingredient.ingredients.render(Arrays.asList(array));
        return content;
    }
}
