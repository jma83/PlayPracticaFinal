package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Recipe;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;
import java.util.Arrays;
import java.util.Optional;

public class RecipeController extends BaseController {


    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-Recipes-Count";

    public Result createRecipe(Http.Request request){
        Result res = null;
        Form<Recipe> form = null;
        Recipe recipe = new Recipe();

        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(recipe.getTitleXML());
            Recipe r = (Recipe) createWithXML(modelNode,recipe).get(0);
            form = formFactory.form(Recipe.class).fill(r);
        }else if (json != null){
            form = formFactory.form(Recipe.class).bindFromRequest(request);
        }else{
            res = Results.badRequest(noResults);
        }

        recipe = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        recipe.save();
        modelList.add(recipe);
        System.out.println("Recipe inserted: " + recipe);


        if (res==null)
            res = this.contentNegotiation(request,getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result getRecipe(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        modelList.addAll(Recipe.findAll());
        if (modelList.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexRecipe(index.get());

        if (res == null)
            res = this.contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexRecipe(String index){
        Result res = null;

        System.out.println(index);
        modelList.clear();
        try {
            Recipe r = Recipe.findById(Integer.parseInt(index));
            if (r != null) {
                modelList.add(r);
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
            recipeUpdate.update(recipe);
            this.modelList.add(recipe);
            recipeUpdate.update();
            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result deleteRecipe(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            Recipe recipeUpdate = Recipe.findById(id);
            this.modelList.add(recipeUpdate);
            recipeUpdate.delete();
            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }
    public Content getContentXML(){
        Recipe[] array = new Recipe[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.Recipe.recipes.render(Arrays.asList(array));
        return content;
    }
}
