package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.RecipeBook;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

public class RecipeBookController extends BaseController {

    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-RecipeBook-Count";

    public Result createRecipeBook(Http.Request request){
        Result res = null;
        Form<RecipeBook> form = null;
        RecipeBook recipeBook = new RecipeBook();

        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(recipeBook.getTitleXML());
            RecipeBook r = (RecipeBook) createWithXML(modelNode,recipeBook).get(0);
            form = formFactory.form(RecipeBook.class).fill(r);
        }else if (json != null){
            form = formFactory.form(RecipeBook.class).bindFromRequest(request);
        }else{
            res = Results.badRequest(noResults);
        }

        recipeBook = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        recipeBook.save();
        modelList.add(recipeBook);
        System.out.println("RecipeBook inserted: " + recipeBook);


        if (res==null)
            res = this.contentNegotiation(request,getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result getRecipeBook(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        modelList.addAll(RecipeBook.findAll());
        if (modelList.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexRecipeBook(index.get());

        if (res == null)
            res = this.contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexRecipeBook(String index){
        Result res = null;

        System.out.println(index);
        modelList.clear();
        try {
            RecipeBook r = RecipeBook.findById(Integer.parseInt(index));
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

    public Result updateRecipeBook(Http.Request request){
        Result res = null;
        Form<RecipeBook> form = formFactory.form(RecipeBook.class).bindFromRequest(request);
        Optional<String> index = request.queryString("index");
        RecipeBook recipe = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        if (recipe != null && res == null && index.isPresent()){
            Long id = Long.valueOf(index.get());
            RecipeBook recipeUpdate = RecipeBook.findById(id);
            recipeUpdate.update(recipe);
            this.modelList.add(recipe);
            recipeUpdate.update();
            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result deleteRecipeBook(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            RecipeBook recipeUpdate = RecipeBook.findById(id);
            this.modelList.add(recipeUpdate);
            recipeUpdate.delete();
            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }


    public Content getContentXML(){
        RecipeBook[] array = new RecipeBook[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.RecipeBook.recipeBooks.render(Arrays.asList(array));
        return content;
    }
}
