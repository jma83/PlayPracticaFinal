package controllers;

import actionCompostionAuth.Attrs;
import actionCompostionAuth.UserArg;
import actionCompostionAuth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Content;
import utils.MessageUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class RecipeBookController extends BaseController {

    String headerCount = "X-RecipeBook-Count";
    public RecipeBookController() {
        super();
    }
    @Inject
    public RecipeBookController(MessagesApi messagesApi){
        super(messagesApi);
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getRecipeBook(Http.Request request){
        initRequest(request);

        Result res = this.getModel(request,this, RecipeBook.findAll());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result getRecipeBookId(Http.Request request, String id){
        initRequest(request);

        User user = request.attrs().get(Attrs.USER);
        RecipeBook r = RecipeBook.findById(checkUserId(user, id,1));

        Result res = getModelId(request,this,r);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result updateRecipeBookProps(Http.Request request, String id){
        initRequest(request);
        Form<RecipeBook> form = formFactory.form(RecipeBook.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            User user = request.attrs().get(Attrs.USER);
            RecipeBook recipeBookUpdate = RecipeBook.findById(checkSelfId(user,id,1));
            RecipeBook recipeBookRequest = form.get();
            if (recipeBookUpdate != null && recipeBookRequest != null) {
                recipeBookUpdate.update(recipeBookRequest);
                res = saveModelResult(request,this,recipeBookUpdate,0,true);
            }
            if (res == null)
                res = contentNegotiationError(request,getMessage(MessageUtils.forbiddenError),403);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result resetRecipeBook(Http.Request request, String id){
        initRequest(request);
        Result res = null;
        User user = request.attrs().get(Attrs.USER);
        RecipeBook recFinal = RecipeBook.findById(checkSelfId(user,id,1));
        if (recFinal != null) {
            recFinal.reset();
            res = saveModelResult(request,this,recFinal,0,true);
        }

        if (res == null)
            res = contentNegotiationError(request,getMessage(MessageUtils.forbiddenError),403);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    //Recipe management:

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result addRecipeById(Http.Request request, String id, Long id2){
        initRequest(request);
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        if (checkSelfId(user, id,1) != -1L){
            Recipe recipe = Recipe.findById(id2);
            RecipeBook rb = user.getRecipeBook();

            if (recipe != null && rb != null) {
                if (RecipeBook.findByRecipe(rb.getId(), recipe) == null) {
                    rb.getRecipeList().add(recipe);
                    res = saveModelResult(request,this,rb,0,true);
                }
            }
        }

        if (res == null)
            res = contentNegotiationError(request,getMessage(MessageUtils.forbiddenError),403);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result addRecipe(Http.Request request, String id){
        initRequest(request);
        Result res = null;

        User user = request.attrs().get(Attrs.USER);
        if (checkSelfId(user, id,1) != -1L){
            RecipeController recipeController = new RecipeController();
            Form<Recipe> form = formFactory.form(Recipe.class);
            form = recipeController.validateRequestForm(request, form);
            res = checkFormErrors(request, form);
            if (res == null) {
                Recipe recipe = form.get();
                RecipeBook rb = user.getRecipeBook();
                if (rb != null && recipe != null) {
                    recipe.setIngredientList(Ingredient.findAndMergeIngredientList(recipe.getIngredientList()));
                    recipe.setTagList(Tag.findAndMergeTagList(recipe.getTagList()));
                    rb.getRecipeList().add(recipe);
                    int count = 0;
                    if (Recipe.findByIdAndRecipeBookId(recipe.getId(),rb.getId()) != null) count = 1;
                    res = saveModelResult(request, this, rb, count, true);
                }
            }
        }

        if (res == null)
            res = contentNegotiationError(request,getMessage(MessageUtils.forbiddenError),403);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result getRecipe(Http.Request request, String id){
        initRequest(request);

        User user = request.attrs().get(Attrs.USER);
        List<Recipe> recipeList = Recipe.findByRecipeBookId(checkUserId(user,id,1));

        Result res = this.getModel(request,this,recipeList);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result getRecipeId(Http.Request request, String id, Long id2){
        initRequest(request);

        User user = request.attrs().get(Attrs.USER);
        Recipe recipe = Recipe.findByIdAndRecipeBookId(id2, checkUserId(user,id,1));

        Result res = this.getModelId(request,this,recipe);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }


    @Security.Authenticated(UserAuthenticator.class)
    @UserArg
    public Result removeRecipe(Http.Request request, String id, Long id2){
        initRequest(request);
        Result res = null;
        User user = request.attrs().get(Attrs.USER);

        Long idRecipeBook = checkSelfId(user,id,1);
        if (idRecipeBook != -1L) {
            RecipeBook rb = RecipeBook.findById(idRecipeBook);
            Recipe recipe = Recipe.findByIdAndRecipeBookId(id2, idRecipeBook);
            if (rb != null && recipe != null) {
                rb.recipeList.remove(recipe);
                res = saveModelResult(request, this, rb, 0,true);
            }
        }

        if (res == null)
            res = contentNegotiationError(request,getMessage(MessageUtils.forbiddenError),403);


        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Form<RecipeBook> validateRequestForm(Http.Request request, Form<RecipeBook> form){
        RecipeBook recipeBook = new RecipeBook();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(recipeBook.getTitleXML());
            RecipeBook r = (RecipeBook) this.xmlManager.createWithXML(modelNode,recipeBook).get(0);
            form.fill(r);
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }

        return form;
    }

    public Content getContentXML(List<BaseModel> modelList){
        RecipeBook[] array = new RecipeBook[modelList.size()];
        modelList.toArray(array);
        return views.xml.RecipeBook.recipeBooks.render(Arrays.asList(array));
    }
}
