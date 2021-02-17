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
import java.util.Optional;

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
            if (!saveModel(i, 0)){
                res = contentNegotiationError(request,duplicatedError,406);
            }
        }

        if (res==null)
            res = contentNegotiation(request, getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredient(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);

        modelList.addAll(Ingredient.findAll());
        if (modelList.size() == 0)
            res = contentNegotiationError(request,noResults,404);

        if (res == null && index.isPresent())
            res = getIndexIngredient(request,index.get());

        if (res == null)
            res = contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexIngredient(Http.Request request, String index){
        clearModelList();
        Result res = null;

        try {
            Ingredient i = Ingredient.findById(Long.valueOf(index));
            if (i != null) {
                modelList.add(i);
            }else{
                res = contentNegotiationError(request,noResults,404);
            }
        } catch (NumberFormatException e) {
            res = contentNegotiationError(request,formatError,400);
        }

        return res;
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result updateIngredient(Http.Request request){
        clearModelList();
        Form<Ingredient> form = formFactory.form(Ingredient.class);
        form = validateRequestForm(request,form);
        Optional<String> index = request.queryString(idQuery);

        Result res = checkFormErrors(request,form);
        if (res == null && index.isPresent()) {
            Long id = Long.valueOf(index.get());
            Ingredient ingredientUpdate = Ingredient.findById(id);
            ingredientUpdate.update(form.get());
            if (!updateModel(ingredientUpdate))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request, getContentXML());
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteIngredient(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            Ingredient ingrFinal = Ingredient.findById(id);

            if (!deleteModel(ingrFinal,true))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request,getContentXML());

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
