package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
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

//FormFactory (solo post y get)
public class RecipeController {
    @Inject
    FormFactory formFactory;

    List<Recipe> recipe = new ArrayList<>();

    public Result createRecipe(Http.Request request){
        Form<Recipe> form = formFactory.form(Recipe.class).bindFromRequest(request);
        Recipe r = form.get();
        Result res = null;
        //Map<String,String> map = form.rawData();

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }


        //insertar usuario
        this.recipe.add(r);

        if (res==null)
            res = this.contentNegotiation(request,this.recipe);

        return res.withHeader("X-User-Count","0");
    }

    public Result getRecipe(Http.Request request){
        Result res = null;

        if (recipe.size() == 0) {
            res = Results.notFound("Sin resultados!");
        }else {

            Optional<String> index = request.queryString("index");

            if (index.isPresent()) {
                System.out.println(index.get());
                try {
                    res = this.getConIndex(Integer.parseInt(index.get()),request);
                } catch (NumberFormatException e) {
                    System.err.println("Error formato no numerico");
                    res = Results.badRequest("Error formato no numerico");

                }
            }
            if (res == null)
                res = this.contentNegotiation(request,this.recipe);

        }


        return res.withHeader("X-User-Count",String.valueOf(recipe.size()));

    }

    public Result getConIndex(int in,Http.Request request){
        Result res = null;
        if (in < 0 || recipe.size() <= in){
            res = Results.notFound("GET - Sin resultados");
        }else if (recipe.get(in) == null) {
            res = Results.notFound("GET - Sin resultados");
        }

        if (res == null) {
            List<Recipe> rec = new ArrayList<>();
            rec.add(this.recipe.get(in));
            res = this.contentNegotiation(request,rec);
        }

        return res;
    }

    public Result contentNegotiation(Http.Request request,List<Recipe> recipe){
        Result res = null;
        if (request.accepts("application/xml")){
            Content content = views.xml.Recipe.recipes.render(recipe);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            res = Results.ok(Json.toJson(recipe));
        }else{
            res = Results.badRequest();
        }

        return res;
    }
}
