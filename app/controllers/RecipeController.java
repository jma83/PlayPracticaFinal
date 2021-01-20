package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Ingredient;
import models.Recipe;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeController {
    @Inject
    FormFactory formFactory;

    List<Recipe> recipes = new ArrayList<>();
    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-Recipes-Count";

    public Result createRecipe(Http.Request request){
        Form<Recipe> form = formFactory.form(Recipe.class).bindFromRequest(request);
        Recipe recipe = form.get();
        Result res = null;

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        recipe.save();
        recipes.add(recipe);
        System.out.println("Recipe inserted: " + recipe);
        if (res==null)
            res = this.contentNegotiation(request,this.recipes);

        return res.withHeader(headerCount,String.valueOf(recipes.size()));
    }

    public Result getRecipe(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        recipes = Recipe.findAll();
        if (recipes.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexRecipe(index.get());

        if (res == null)
            res = this.contentNegotiation(request,this.recipes);


        return res.withHeader(headerCount,String.valueOf(recipes.size()));

    }

    public Result getIndexRecipe(String index){
        Result res = null;

        System.out.println(index);
        recipes.clear();
        try {
            Recipe r = Recipe.findById(Integer.parseInt(index));
            if (r != null) {
                recipes.add(r);
            }else{
                res = Results.notFound(noResults);
            }
        } catch (NumberFormatException e) {
            res = Results.badRequest(formatError);
        }

        return res;
    }

    public Result updateRecipe(Http.Request request){
        Result res = null;
        Form<Recipe> form = formFactory.form(Recipe.class).bindFromRequest(request);
        Optional<String> index = request.queryString("index");
        Recipe recipe = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        if (recipe != null && res == null && index.isPresent()){
            Long id = Long.valueOf(index.get());
            Recipe recipeUpdate = Recipe.findById(id);
            recipeUpdate.updateRecipe(recipe);
            this.recipes.add(recipe);
            recipeUpdate.update();
            res = this.contentNegotiation(request,this.recipes);
        }
        return res.withHeader(headerCount,String.valueOf(recipes.size()));
    }

    public Result deleteRecipe(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            Recipe recipeUpdate = Recipe.findById(id);
            this.recipes.add(recipeUpdate);
            recipeUpdate.delete();
            res = this.contentNegotiation(request,this.recipes);
        }
        return res.withHeader(headerCount,String.valueOf(recipes.size()));
    }

    public Result contentNegotiation(Http.Request request,List<Recipe> recipes){
        Result res = null;
        if (request.accepts("application/xml")){
            Content content = views.xml.Recipe.recipes.render(recipes);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            //res = Results.ok(Json.toJson(recipes));
            ObjectMapper mapper = new ObjectMapper();
            try {
                String result = mapper.writeValueAsString(recipes);
                res = Results.ok(Json.parse(result));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else{
            res = Results.badRequest();
        }

        this.recipes.clear();

        return res;
    }
}
