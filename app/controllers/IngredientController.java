package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Ingredient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import java.util.Arrays;
import java.util.Optional;

public class IngredientController extends BaseController {

    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-Ingredient-Count";

    public Result createIngredient(Http.Request request){
        Result res = null;
        Form<Ingredient> form = null;
        Ingredient ingredient = new Ingredient();

        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(ingredient.getTitleXML());
            Ingredient i = (Ingredient) createWithXML(modelNode,ingredient).get(0);
            form = formFactory.form(Ingredient.class).fill(i);
        }else if (json != null){
            form = formFactory.form(Ingredient.class).bindFromRequest(request);
        }else{
            res = Results.badRequest(noResults);
        }

        ingredient = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        ingredient.save();
        modelList.add(ingredient);
        System.out.println("Ingredient inserted: " + ingredient);


        if (res==null)
            res = this.contentNegotiation(request,getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result getIngredient(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        modelList.addAll(Ingredient.findAll());
        if (modelList.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexIngredient(index.get());

        if (res == null)
            res = this.contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexIngredient(String index){
        Result res = null;

        System.out.println(index);
        modelList.clear();
        try {
            Ingredient i = Ingredient.findById(Integer.parseInt(index));
            if (i != null) {
                modelList.add(i);
            }else{
                res = Results.notFound(noResults);
            }
        } catch (NumberFormatException e) {
            res = Results.badRequest(formatError);
        }

        return res;
    }

    public Result updateIngredient(Http.Request request){
        Result res = null;
        Form<Ingredient> form = formFactory.form(Ingredient.class).bindFromRequest(request);
        Optional<String> index = request.queryString("index");
        Ingredient ingredient = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        if (ingredient != null && res == null && index.isPresent()){
            Long id = Long.valueOf(index.get());
            Ingredient ingredientUpdate = Ingredient.findById(id);
            ingredientUpdate.update(ingredient);
            modelList.add(ingredientUpdate);
            ingredientUpdate.update();
            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Result deleteIngredient(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            Ingredient ingrFinal = Ingredient.findById(id);
            this.modelList.add(ingrFinal);
            ingrFinal.delete();

            res = this.contentNegotiation(request,getContentXML());
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Content getContentXML(){
        Ingredient[] array = new Ingredient[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.Ingredient.ingredients.render(Arrays.asList(array));
        return content;
    }
}
