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

    List<RecipeBook> recipeBookList = new ArrayList<>();
    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-RecipeBook-Count";

    public Result createRecipeBook(Http.Request request){
        Form<RecipeBook> form = formFactory.form(RecipeBook.class).bindFromRequest(request);
        RecipeBook recipeBook = form.get();
        Result res = null;

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        recipeBook.save();
        recipeBookList.add(recipeBook);
        System.out.println("RecipeBook inserted: " + recipeBook);
        if (res==null)
            res = this.contentNegotiation(request,this.recipeBookList);

        return res.withHeader(headerCount,String.valueOf(recipeBookList.size()));
    }

    public Result getRecipeBook(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        recipeBookList = RecipeBook.findAll();
        if (recipeBookList.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexRecipeBook(index.get());

        if (res == null)
            res = this.contentNegotiation(request,this.recipeBookList);


        return res.withHeader(headerCount,String.valueOf(recipeBookList.size()));

    }

    public Result getIndexRecipeBook(String index){
        Result res = null;

        System.out.println(index);
        recipeBookList.clear();
        try {
            RecipeBook r = RecipeBook.findById(Integer.parseInt(index));
            if (r != null) {
                recipeBookList.add(r);
            }else{
                res = Results.notFound(noResults);
            }
        } catch (NumberFormatException e) {
            res = Results.badRequest(formatError);
        }

        return res;
    }

    public Result contentNegotiation(Http.Request request,List<RecipeBook> recipeBookList){
        Result res = null;
        if (request.accepts("application/xml")){
            Content content = views.xml.RecipeBook.recipeBooks.render(recipeBookList);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            res = Results.ok(Json.toJson(recipeBookList));
        }else{
            res = Results.badRequest();
        }

        this.recipeBookList.clear();

        return res;
    }
}
