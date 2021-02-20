package controllers;

import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
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
    public Result getRecipe(Http.Request request){
        clearModelList();
        Result res = null;

        modelList.addAll(Recipe.findAll());
        res = this.getModel(request,this);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }
    @Security.Authenticated(UserAuthenticator.class)
    public Result getRecipeId(Http.Request request, Long id){
        clearModelList();
        Result res = null;

        try {
            Recipe r = Recipe.findById(id);
            if (r != null) {
                modelList.add(r);
                res = contentNegotiation(request,getContentXML());
            }else{
                res = contentNegotiationError(request,noResults,404);
            }
        } catch (NumberFormatException e) {
            res = contentNegotiationError(request,formatError,400);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result updateRecipe(Http.Request request, Long id){
        clearModelList();
        Form<Recipe> form = formFactory.form(Recipe.class);
        form = validateRequestForm(request,form);

        Result res = checkFormErrors(request,form);
        if (res == null) {
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
    public Result deleteRecipe(Http.Request request, Long id){
        clearModelList();
        Result res = null;
        Recipe recFinal = Recipe.findById(id);

        if (!deleteModel(recFinal,true))
            res = contentNegotiationError(request,noResults,404);
        else
            res = contentNegotiation(request,getContentXML());


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

    @Security.Authenticated(UserAuthenticator.class)
    public Result addIngredient(Http.Request request, Long id){
        clearModelList();
        Result res = null;

        //Optional<String> in = request.queryString(idQuery);

        getRecipeId(request,id);
        if (modelList.size() == 1){
            Recipe recipe = (Recipe) modelList.get(0);
            IngredientController ingredientController = new IngredientController();
            Form<Ingredient> form = formFactory.form(Ingredient.class);
            form = ingredientController.validateRequestForm(request,form);
            res = ingredientController.checkFormErrors(request,form);
            if (res == null){
                Ingredient ingredient = form.get();
                if (!recipe.checkIngredient(ingredient)) {
                    recipe.ingredientList.add(ingredient);
                    clearModelList();
                    if (saveModel(recipe, 0)){
                        res = contentNegotiation(request, getContentXML());
                    }
                }
                if (res == null)
                    res = contentNegotiationError(request,duplicatedError,406);

            }
            if (res == null)
                contentNegotiationError(request,formatError,400);

        }


        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }




    @Security.Authenticated(UserAuthenticator.class)
    public Result removeIngredient(Http.Request request, Long id, Long id2){
        clearModelList();
        Result res = null;
        //Optional<String> in = request.queryString(idQuery);

        getRecipeId(request,id);
        if (modelList.size() == 1){
            Recipe recipe = (Recipe) modelList.get(0);

            Ingredient ingredient = Ingredient.findById(id2);
            if (recipe.checkIngredient(ingredient)) {
                recipe.ingredientList.remove(ingredient);
                clearModelList();
                if (saveModel(recipe, 0)){
                    res = contentNegotiation(request, getContentXML());
                }
            }
            if (res == null)
                res = contentNegotiationError(request,duplicatedError,406);

        }


        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredient(Http.Request request, Long id){
        clearModelList();
        Result res = null;
        //Optional<String> in = request.queryString(idQuery);
        IngredientController ingredientController = new IngredientController();
        getRecipeId(request,id);
        if (modelList.size() == 1){
            Recipe recipe = (Recipe) modelList.get(0);
            clearModelList();
            modelList.addAll(recipe.getIngredientList());
            res = contentNegotiation(request,ingredientController.getContentXML());

        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }
    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredientId(Http.Request request, Long id, Long id2){
        clearModelList();
        Result res = null;
        //Optional<String> in = request.queryString(idQuery);
        IngredientController ingredientController = new IngredientController();
        getRecipeId(request,id);
        if (modelList.size() == 1){
            Recipe recipe = (Recipe) modelList.get(0);

            Ingredient ingredient = Ingredient.findById(id2);
            if (recipe.checkIngredient(ingredient)) {
                clearModelList();
                modelList.add(ingredient);
                res = contentNegotiation(request,ingredientController.getContentXML());
            }

            if (res == null)
                res = contentNegotiationError(request,formatError,400);

        }


        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }
}
