package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.RecipeBook;
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
public class RecipeBookController {
    @Inject
    FormFactory formFactory;

    List<RecipeBook> recipeBook = new ArrayList<>();

    public Result createRecipeBook(Http.Request request){
        Form<RecipeBook> form = formFactory.form(RecipeBook.class).bindFromRequest(request);
        RecipeBook rb = form.get();
        Result res = null;
        //Map<String,String> map = form.rawData();

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }


        //insertar usuario
        this.recipeBook.add(rb);

        if (res==null)
            res = this.contentNegotiation(request,this.recipeBook);

        return res.withHeader("X-User-Count","0");
    }

    public Result getRecipeBook(Http.Request request){
        Result res = null;

        if (recipeBook.size() == 0) {
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
                res = this.contentNegotiation(request,this.recipeBook);

        }


        return res.withHeader("X-User-Count",String.valueOf(recipeBook.size()));

    }

    public Result getConIndex(int in,Http.Request request){
        Result res = null;
        if (in < 0 || recipeBook.size() <= in){
            res = Results.notFound("GET - Sin resultados");
        }else if (recipeBook.get(in) == null) {
            res = Results.notFound("GET - Sin resultados");
        }

        if (res == null) {
            List<RecipeBook> ingr = new ArrayList<>();
            ingr.add(this.recipeBook.get(in));
            res = this.contentNegotiation(request,ingr);
        }

        return res;
    }

    public Result contentNegotiation(Http.Request request,List<RecipeBook> ingr){
        Result res = null;
        if (request.accepts("application/xml")){
            Content content = views.xml.RecipeBook.recipeBooks.render(ingr);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            res = Results.ok(Json.toJson(ingr));
        }else{
            res = Results.badRequest();
        }

        return res;
    }
}
