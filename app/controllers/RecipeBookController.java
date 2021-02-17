package controllers;

import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.RecipeBook;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Content;

import java.util.Arrays;
import java.util.Optional;

public class RecipeBookController extends BaseController {

    String headerCount = "X-RecipeBook-Count";

    @Security.Authenticated(UserAuthenticator.class)
    public Result createRecipeBook(Http.Request request){
        clearModelList();
        Form<RecipeBook> form = formFactory.form(RecipeBook.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            RecipeBook r = form.get();
            //r.init();
            if (!saveModel(r, 0)){
                res = contentNegotiationError(request,duplicatedError,406);
            }
        }

        if (res==null)
            res = contentNegotiation(request, getContentXML());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }
    @Security.Authenticated(UserAuthenticator.class)
    public Result getRecipeBook(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);

        modelList.addAll(RecipeBook.findAll());
        if (modelList.size() == 0)
            res = contentNegotiationError(request,noResults,404);

        if (res == null && index.isPresent())
            res = getIndexRecipeBook(request,Long.valueOf(index.get()));

        if (res == null)
            res = contentNegotiation(request,getContentXML());


        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    public Result getIndexRecipeBook(Http.Request request, Long index){
        clearModelList();
        Result res = null;

        try {
            RecipeBook r = RecipeBook.findById(index);
            if (r != null) {
                modelList.add(r);
            }else{
                res = contentNegotiationError(request,noResults,404);
            }
        } catch (NumberFormatException e) {
            res = contentNegotiationError(request,formatError,400);
        }

        return res;
    }


    @Security.Authenticated(UserAuthenticator.class)
    public Result updateRecipeBook(Http.Request request){
        clearModelList();
        Form<RecipeBook> form = formFactory.form(RecipeBook.class);
        form = validateRequestForm(request,form);
        Optional<String> index = request.queryString(idQuery);

        Result res = checkFormErrors(request,form);
        if (res == null && index.isPresent()) {
            Long id = Long.valueOf(index.get());
            RecipeBook recipeUpdate = RecipeBook.findById(id);
            recipeUpdate.update(form.get());
            if (!updateModel(recipeUpdate))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request, getContentXML());
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteRecipeBook(Http.Request request){
        clearModelList();
        Result res = null;
        Optional<String> index = request.queryString(idQuery);
        if (index.isPresent()){
            Long id2 = Long.valueOf(index.get());
            RecipeBook recFinal = RecipeBook.findById(id2);


            if (!deleteModel(recFinal,true))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request,getContentXML());

        }else {
            res = contentNegotiationError(request, missingId, 400);
        }
        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Form<RecipeBook> validateRequestForm(Http.Request request, Form<RecipeBook> form){
        RecipeBook recipeBook = new RecipeBook();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(recipeBook.getTitleXML());
            RecipeBook r = (RecipeBook) createWithXML(modelNode,recipeBook).get(0);
            form.fill(r);
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }

        return form;
    }

    public Content getContentXML(){
        RecipeBook[] array = new RecipeBook[modelList.size()];
        modelList.toArray(array);
        Content content = views.xml.RecipeBook.recipeBooks.render(Arrays.asList(array));
        return content;
    }
}
