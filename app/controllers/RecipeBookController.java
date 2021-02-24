package controllers;

import auth.Attrs;
import auth.PassArgAction;
import auth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.Ingredient;
import models.Recipe;
import models.RecipeBook;
import models.User;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.twirl.api.Content;
import java.util.Arrays;
import java.util.List;

public class RecipeBookController extends BaseController {

    String headerCount = "X-RecipeBook-Count";


    @Security.Authenticated(UserAuthenticator.class)
    public Result getRecipeBook(Http.Request request){
        clearModelList();
        Result res = null;

        modelList.addAll(RecipeBook.findAll());
        res = this.getModel(request,this);

        if (res == null)
            res = contentNegotiationError(request,noResults,404);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }
    @With(PassArgAction.class)
    @Security.Authenticated(UserAuthenticator.class)
    public Result getRecipeBookId(Http.Request request, String id){
        clearModelList();
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        if (user !=null) {
            RecipeBook r = RecipeBook.findById(checkUserId(user, id));
            if (r != null) {
                modelList.add(r);
                res = contentNegotiation(request, getContentXML());
            } else {
                res = contentNegotiationError(request, noResults, 404);
            }
        }
        if (res == null)
        res = contentNegotiationError(request,formatError,400);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @With(PassArgAction.class)
    @Security.Authenticated(UserAuthenticator.class)
    public Result updateRecipeBook(Http.Request request, String id){
        clearModelList();
        Form<RecipeBook> form = formFactory.form(RecipeBook.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            User user = request.attrs().get(Attrs.USER);
            RecipeBook recipeUpdate = RecipeBook.findById(checkSelfId(user,id));
            recipeUpdate.update(form.get());
            if (!updateModel(recipeUpdate))
                res = contentNegotiationError(request,noResults,404);
            else
                res = contentNegotiation(request, getContentXML());
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @With(PassArgAction.class)
    @Security.Authenticated(UserAuthenticator.class)
    public Result resetRecipeBook(Http.Request request, String id){
        clearModelList();
        Result res = null;
        User user = request.attrs().get(Attrs.USER);
        if (user != null) {
            RecipeBook recFinal = RecipeBook.findById(checkSelfId(user,id));
            if (recFinal != null) {
                recFinal.reset();
                if (saveModel(recFinal, 0)) {
                    res = contentNegotiation(request, getContentXML());
                }
            }
        }
        if (res == null)
            res = contentNegotiationError(request, noResults, 404);

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

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result addRecipeById(Http.Request request, String id, Long id2){
        clearModelList();
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        if (user != null) {
            if (checkSelfId(user, id) != -1L){
                Recipe recipe = Recipe.findById(id2);
                if (recipe != null) {
                    RecipeBook rb = user.getRecipeBook();
                    if (rb != null) {
                        if (RecipeBook.findByRecipe(rb.getId(), recipe) == null) {
                            rb.getRecipeList().add(recipe);
                            if (saveModel(rb, 0)) {
                                res = contentNegotiation(request, getContentXML());
                            }
                        }
                        if (res == null)
                            res = contentNegotiationError(request, duplicatedError, 406);
                    }
                    if (res == null)
                        res = contentNegotiationError(request, this.formatError, 400);
                }
            }
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result addRecipe(Http.Request request, String id){
        clearModelList();
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        if (user != null) {
            if (checkSelfId(user, id) != -1L){
                Form<Recipe> form = formFactory.form(Recipe.class);
                RecipeController recipeController = new RecipeController();
                recipeController.createRecipe(request);
                Recipe recipe = (Recipe) modelList.get(0);
                RecipeBook rb = user.getRecipeBook();
                if (rb != null) {
                    rb.getRecipeList().add(recipe);
                    if (saveModel(rb, 0)) {
                        res = contentNegotiation(request, getContentXML());
                    }

                    if (res == null)
                        res = contentNegotiationError(request, duplicatedError, 406);
                }
                if (res == null)
                    res = contentNegotiationError(request, this.formatError, 400);

            }
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result getRecipes(Http.Request request, String id){
        clearModelList();
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        RecipeBook rb = RecipeBook.findById(checkUserId(user,id));
        if (rb != null) {
            List<Recipe> recipeList = rb.getRecipeList();

            modelList.addAll(recipeList);
            res = this.getModel(request,this);

        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result getRecipe(Http.Request request, String id, Long id2){
        clearModelList();
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        RecipeBook rb = RecipeBook.findById(checkUserId(user,id));
        if (rb != null) {
            Recipe recipe = Recipe.findById(id2);
            if (recipe != null) {
                if (RecipeBook.findByRecipe(rb.getId(),recipe) != null) {
                    modelList.add(recipe);
                    res = this.getModel(request,this);
                }

                if (res == null)
                    res = contentNegotiationError(request, this.formatError, 400);
            }
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }


    @Security.Authenticated(UserAuthenticator.class)
    @With(PassArgAction.class)
    public Result removeRecipe(Http.Request request, String id, Long id2){
        clearModelList();
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        RecipeBook rb = RecipeBook.findById(checkUserId(user,id));
        if (rb != null) {
            Recipe recipe = Recipe.findById(id2);
            if (recipe != null) {
                if (RecipeBook.findByRecipe(rb.getId(),recipe) != null) {
                    rb.recipeList.remove(recipe);
                    if (saveModel(rb,0)){
                        res = contentNegotiation(request, getContentXML());
                    }
                }

                if (res == null)
                    res = contentNegotiationError(request, this.formatError, 400);
            }
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    private Long checkUserId(User u, String id){
        Long res = checkSelfId(u,id);
        if (res != -1) return res;
        try {
            res = Long.valueOf(id);
        }catch (NumberFormatException e){
            System.out.println("Error: " + e.getMessage());
        }

        return res;
    }

    private Long checkSelfId(User u, String id){
        if ("self".equals(id) || id.equals(Long.toString(u.getRecipeBook().getId()))){
            return u.getRecipeBook().getId();
        }
        return -1L;
    }
}
