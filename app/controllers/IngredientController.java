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

//FormFactory (solo post y get)
public class IngredientController {
    @Inject
    FormFactory formFactory;

    List<Ingredient> ingredients = new ArrayList<>();

    public Result createIngredient(Http.Request request){
        Form<Ingredient> form = formFactory.form(Ingredient.class).bindFromRequest(request);
        Ingredient i = form.get();
        Result res = null;
        //Map<String,String> map = form.rawData();

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }


        //insertar usuario
        this.ingredients.add(i);

        if (res==null)
            res = this.contentNegotiation(request,this.ingredients);

        return res.withHeader("X-User-Count","0");
    }

    public Result getIngredient(Http.Request request){
        Result res = null;

        if (ingredients.size() == 0) {
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
                res = this.contentNegotiation(request,this.ingredients);

        }


        return res.withHeader("X-User-Count",String.valueOf(ingredients.size()));

    }

    public Result getConIndex(int in,Http.Request request){
        Result res = null;
        if (in < 0 || ingredients.size() <= in){
            res = Results.notFound("GET - Sin resultados");
        }else if (ingredients.get(in) == null) {
            res = Results.notFound("GET - Sin resultados");
        }

        if (res == null) {
            List<Ingredient> ingr = new ArrayList<>();
            ingr.add(this.ingredients.get(in));
            res = this.contentNegotiation(request,ingr);
        }

        return res;
    }

    public Result contentNegotiation(Http.Request request,List<Ingredient> ingr){
        Result res = null;
        if (request.accepts("application/xml")){
            Content content = views.xml.Ingredient.ingredients.render(ingr);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            res = Results.ok(Json.toJson(ingr));
        }else{
            res = Results.badRequest();
        }

        return res;
    }
}
