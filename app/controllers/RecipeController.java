package controllers;

import auth.Attrs;
import auth.PassArgAction;
import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Content;
import utils.RecipeSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RecipeController extends BaseController {


    String headerCount = "X-Recipe-Count";
    Boolean checkFilter = false;

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result createRecipe(Http.Request request){   //Ok
        clearModelList();
        Form<Recipe> form = formFactory.form(Recipe.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            Recipe r = form.get();
            User user = request.attrs().get(Attrs.USER);
            r.setAuthor(user);
            r.setIngredientList(Ingredient.findAndMergeIngredientList(r.getIngredientList()));
            int count = Recipe.findByNameAndUser(r.getName(),user,r.getId()).size();
            res = saveModelResult(request,this,r,count,false);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result getRecipe(Http.Request request){  //OK
        clearModelList();
        User user = request.attrs().get(Attrs.USER);
        List<Recipe> recipeList = this.filterRecipe(request,user);
        Result res;

        if (!checkFilter) {
            res = this.getModel(request,this,Recipe.findAll());
        }else{
            res = this.getModel(request,this,recipeList);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getRecipeId(Http.Request request, Long id){   //OK
        clearModelList();

        Recipe r = Recipe.findById(id);
        Result res = getModelId(request,this,r);

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
            Recipe recipeRequest = form.get();
            if (recipeUpdate != null && recipeRequest != null) {
                recipeUpdate.update(recipeRequest);
                recipeUpdate.setIngredientList(Ingredient.findAndMergeIngredientList(recipeUpdate.getIngredientList()));
                int count = Recipe.findByNameAndUser(recipeUpdate.getName(),recipeUpdate.getAuthor(),recipeUpdate.getId()).size();
                res = saveModelResult(request,this,recipeUpdate, count,true);
            }
            if (res == null)
                res = contentNegotiationError(request,noResults,404);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteRecipe(Http.Request request, Long id){
        clearModelList();
        Recipe recFinal = Recipe.findById(id);
        Result res = deleteModelResult(request,this,recFinal);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }


    //Ingredient management:

    @Security.Authenticated(UserAuthenticator.class)
    public Result addIngredient(Http.Request request, Long id){
        clearModelList();
        IngredientController ingredientController = new IngredientController();
        Form<Ingredient> form = formFactory.form(Ingredient.class);
        form = ingredientController.validateRequestForm(request, form);
        Result res = checkFormErrors(request, form);

        if (res == null) {
            Recipe recipe = Recipe.findById(id);
            Ingredient ingredient = form.get();
            if (recipe != null && ingredient != null) {
                List<Ingredient> ingredients = new ArrayList<>();
                ingredients.add(ingredient);
                int count = Ingredient.findByNameAndRecipeId(ingredient.getName(), recipe.getId()).size();
                Ingredient i = Ingredient.findAndMergeIngredientList(ingredients).get(0);
                recipe.ingredientList.add(i);
                res = saveModelResult(request, this, recipe, count,true);
            }
            if (res == null)
                res = contentNegotiationError(request,noResults,404);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result addIngredientById(Http.Request request, Long id, Long id2){
        clearModelList();
        Result res = null;
        Recipe recipe = Recipe.findById(id);
        Ingredient ingredient = Ingredient.findByIdAndRecipeId(id2, id);

        if (recipe != null && ingredient != null) {
            recipe.ingredientList.add(ingredient);
            res = saveModelResult(request,this,recipe,0,true);
        }
        if (res == null)
            res = contentNegotiationError(request,noResults,404);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result removeIngredient(Http.Request request, Long id, Long id2){
        clearModelList();
        Result res = null;
        Recipe recipe = Recipe.findById(id);
        Ingredient ingredient = Ingredient.findByIdAndRecipeId(id2, id);

        if (recipe != null && ingredient != null) {
            recipe.ingredientList.remove(ingredient);
            res = saveModelResult(request,this,recipe,0,true);
        }
        if (res == null)
            res = contentNegotiationError(request,noResults,404);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredient(Http.Request request, Long id){
        clearModelList();

        List<Ingredient> ingredients = Ingredient.findByRecipeId(id);
        Result res = this.getModel(request,new IngredientController(),ingredients);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredientId(Http.Request request, Long id, Long id2){
        clearModelList();

        Ingredient ingredient = Ingredient.findByIdAndRecipeId(id2, id);
        Result res = this.getModelId(request,new IngredientController(),ingredient);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }


    public List<Recipe> filterRecipe(Http.Request request, User userRequest){
        List<Recipe> recipeList = null;
        RecipeSearch recipeSearch = new RecipeSearch(request);
        User usuFinal = User.findById(checkSelfId(userRequest,recipeSearch.getAuthorId(),0));

        if (usuFinal!=null)
            recipeSearch.setAuthorId(Optional.of(String.valueOf(usuFinal.getId())));

        checkFilter = recipeSearch.checkNotNulls();

        if (checkFilter) recipeList = Recipe.findByFilter(recipeSearch);

        return recipeList;
    }

    public Form<Recipe> validateRequestForm(Http.Request request, Form<Recipe> form){
        Recipe recipe = new Recipe();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(recipe.getTitleXML());
            Recipe r = (Recipe) this.xmlManager.createWithXML(modelNode,recipe).get(0);
            form.fill(r);
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }

        return form;
    }

    public Content getContentXML(List<BaseModel> modelList){
        Recipe[] array = new Recipe[modelList.size()];
        modelList.toArray(array);
        return views.xml.Recipe.recipes.render(Arrays.asList(array));
    }
}
