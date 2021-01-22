package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Ingredient;
import models.User;
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

        if (index.isPresent() && res==null) {
            System.out.println("index is present");
            res = this.getIndexIngredient(index.get());
            System.out.println("index is present out");
            System.out.println("Res: " + res);

        }

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
                System.out.println("Ingredient found!");
                ingredients.add(i);
            }else{
                System.out.println("notFound");
                res = Results.notFound(noResults);
            }
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException");
            res = Results.badRequest(formatError);
        }
        System.out.println("res");
        System.out.println(res);
        return res;
    }

    public Result updateIngredient(Http.Request request){
        Result res = null;
        Form<Ingredient> form = formFactory.form(Ingredient.class).bindFromRequest(request);
        Optional<String> index = request.queryString("index");
        Ingredient ingr = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        if (ingr != null && res == null && index.isPresent()){
            Long id = Long.valueOf(index.get());
            Ingredient ingrUpdate = Ingredient.findById(id);
            if (ingrUpdate!=null) {
                ingrUpdate.updateIngredient(ingr);
                this.ingredients.add(ingrUpdate);
                ingrUpdate.update();
            }else{
                res = Results.notFound(noResults);
            }
            res = this.contentNegotiation(request,this.ingredients);
        }
        return res.withHeader(headerCount,String.valueOf(ingredients.size()));
    }

    public Result deleteIngredient(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            Ingredient ingrUpdate = Ingredient.findById(id);
            this.ingredients.add(ingrUpdate);
            ingrUpdate.delete();
            res = this.contentNegotiation(request,this.ingredients);
        }
        return res.withHeader(headerCount,String.valueOf(ingredients.size()));
    }

    public Result contentNegotiation(Http.Request request,List<Ingredient> ingredients){
        Result res = null;
        if (request.accepts("application/xml")){
            System.out.println("xml " + ingredients);

            Content content = views.xml.Ingredient.ingredients.render(ingredients);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            System.out.println("json " + ingredients);
            //res = Results.ok(Json.toJson(ingredients));
            ObjectMapper mapper = new ObjectMapper();
            try {
                String result = mapper.writeValueAsString(ingredients);
                res = Results.ok(Json.parse(result));
                System.out.println("Res " + res);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }else{
            res = Results.badRequest();
        }

        this.ingredients.clear();

        return res;
    }
}
