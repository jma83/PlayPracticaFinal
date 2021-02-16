package controllers;

import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.BaseModel;
import models.Ingredient;
import models.Recipe;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Content;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RecipeController extends BaseController {


    String headerCount = "X-Recipe-Count";


    public Result createRecipe(Http.Request request){
        clearModelList();
        Form<Recipe> form = formFactory.form(Recipe.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            Recipe r = form.get();
            //r.init();
            if (!saveModel(r, 0)){
                res = contentNegotiationError(request,duplicatedError,406);
            }
        }

        if (res==null)
            res = contentNegotiation(request, getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result addIngredient(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> id = request.queryString(idQuery);

        if (id.isPresent()){
            res = getIndexRecipe(request,id.get());
            if (res == null && modelList.size() == 1){
                Recipe recipe = (Recipe) modelList.get(0);
                IngredientController ingredientController = new IngredientController();
                Form<Ingredient> form = formFactory.form(Ingredient.class);
                form = ingredientController.validateRequestForm(request,form);
                res = ingredientController.checkFormErrors(request,form);
                if (res == null){

                }
            }


        }

        return res;
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getRecipe(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);

        modelList.addAll(Recipe.findAll());
        if (modelList.size() == 0)
            res = contentNegotiationError(request,noResults,404);

        if (res == null && index.isPresent())
            res = getIndexRecipe(request,index.get());

        if (res == null)
            res = contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexRecipe(Http.Request request, String index){
        clearModelList();
        Result res = null;

        try {
            Recipe r = Recipe.findById(Long.valueOf(index));
            if (r != null) {
                modelList.add(r);
            }else{
                res = contentNegotiationError(request,noResults,404);
            }
        } catch (NumberFormatException e) {
            res = contentNegotiationError(request,formatError,400);
        }

        return res;
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result updateRecipe(Http.Request request){
        clearModelList();
        Form<Recipe> form = formFactory.form(Recipe.class);
        form = validateRequestForm(request,form);
        Optional<String> index = request.queryString(idQuery);

        Result res = checkFormErrors(request,form);
        if (res == null && index.isPresent()) {
            Long id = Long.valueOf(index.get());
            Recipe recipeUpdate = Recipe.findById(id);
            recipeUpdate.update(form.get());
            if (!updateModel(recipeUpdate))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request, getContentXML());
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteRecipe(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);
        if (index.isPresent()){
            Long id2 = Long.valueOf(index.get());
            Recipe recFinal = Recipe.findById(id2);


            if (!deleteModel(recFinal,true))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request,getContentXML());

        }else {
            res = contentNegotiationError(request, missingId, 400);
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Form<Recipe> validateRequestForm(Http.Request request, Form<Recipe> form){
        Recipe recipe = new Recipe();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(recipe.getTitleXML());
            Recipe r = (Recipe) createWithXML(modelNode,recipe).get(0);
            form.fill(r);
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }

        return form;
    }

    public Content getContentXML(){
        Recipe[] array = new Recipe[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.Recipe.recipes.render(Arrays.asList(array));
        return content;
    }
}
