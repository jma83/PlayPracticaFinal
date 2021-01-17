package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Ingredient;
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

public class IngredientController {
    @Inject
    FormFactory formFactory;

    List<Ingredient> ingredients = new ArrayList<>();
    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-Ingredient-Count";

    public Result createIngredient(Http.Request request){
        Form<Ingredient> form = formFactory.form(Ingredient.class).bindFromRequest(request);
        Ingredient ingredient = form.get();
        Result res = null;

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        ingredient.save();
        ingredients.add(ingredient);
        System.out.println("Ingredient inserted: " + ingredient);
        if (res==null)
            res = this.contentNegotiation(request,this.ingredients);

        return res.withHeader(headerCount,String.valueOf(ingredients.size()));
    }

    public Result getIngredient(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        ingredients = Ingredient.findAll();
        if (ingredients.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexIngredient(index.get());

        if (res == null)
            res = this.contentNegotiation(request,this.ingredients);


        return res.withHeader(headerCount,String.valueOf(ingredients.size()));

    }

    public Result getIndexIngredient(String index){
        Result res = null;

        System.out.println(index);
        ingredients.clear();
        try {
            Ingredient i = Ingredient.findById(Integer.parseInt(index));
            if (i != null) {
                ingredients.add(i);
            }else{
                res = Results.notFound(noResults);
            }
        } catch (NumberFormatException e) {
            res = Results.badRequest(formatError);
        }

        return res;
    }

    public Result contentNegotiation(Http.Request request,List<Ingredient> ingredients){
        Result res = null;
        if (request.accepts("application/xml")){
            Content content = views.xml.Ingredient.ingredients.render(ingredients);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            res = Results.ok(Json.toJson(ingredients));
        }else{
            res = Results.badRequest();
        }

        this.ingredients.clear();

        return res;
    }
}
