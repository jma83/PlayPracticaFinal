package controllers;

import actionCompostionAuth.UserAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.BaseModel;
import models.Ingredient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Content;
import java.util.Arrays;
import java.util.List;

public class IngredientController extends BaseController {

    String headerCount = "X-Ingredient-Count";

    @Security.Authenticated(UserAuthenticator.class)
    public Result createIngredient(Http.Request request){
        clearModelList();
        Form<Ingredient> form = formFactory.form(Ingredient.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            Ingredient i = form.get();
            List<Ingredient> list = Ingredient.findByName(i.getName());
            res = saveModelResult(request,this,i,list.size(),false);
        }

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }


    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredient(Http.Request request){
        clearModelList();

        Result res = this.getModel(request,this, Ingredient.findAll());

        return res.withHeader(headerCount,String.valueOf(modelList.size()));

    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result getIngredientId(Http.Request request, Long id){
        clearModelList();

        Ingredient i = Ingredient.findById(id);
        Result res = getModelId(request,this, i);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result updateIngredient(Http.Request request, Long id){
        clearModelList();
        Form<Ingredient> form = formFactory.form(Ingredient.class);
        form = validateRequestForm(request,form);
        Result res = checkFormErrors(request,form);

        if (res == null) {
            Ingredient ingredientUpdate = Ingredient.findById(id);
            Ingredient ingredientRequest = form.get();
            if (ingredientUpdate != null && ingredientRequest != null) {
                ingredientUpdate.update(ingredientRequest);
                int count = Ingredient.findByName(ingredientUpdate.getName()).size();
                res = saveModelResult(request,this,ingredientUpdate, count,true);
            }
        }

        if (res == null)
            res = contentNegotiationError(request,noResults,404);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    @Security.Authenticated(UserAuthenticator.class)
    public Result deleteIngredient(Http.Request request, Long id){
        clearModelList();
        Ingredient ingrFinal = Ingredient.findById(id);

        Result res = this.deleteModelResult(request,this,ingrFinal);

        return res.withHeader(headerCount,String.valueOf(modelList.size()));
    }

    public Form<Ingredient> validateRequestForm(Http.Request request, Form<Ingredient> form){
        Ingredient ingredient = new Ingredient();
        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            NodeList modelNode = doc.getElementsByTagName(ingredient.getTitleXML());
            form.fill((Ingredient) this.xmlManager.createWithXML(modelNode,ingredient).get(0));
        }else if (json != null){
            form = form.bindFromRequest(request);
        }else{
            form = null;
        }

        return form;
    }

    public Content getContentXML(List<BaseModel> modelList){
        Ingredient[] array = new Ingredient[modelList.size()];
        modelList.toArray(array);
        return views.xml.Ingredient.ingredients.render(Arrays.asList(array));
    }
}
