package controllers.src;

import play.mvc.Http;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class RecipeSearch {
    Optional<String> name;
    Optional<String> description;
    Optional<String> dateLower;
    Optional<String> dateGreater;
    Optional<String> recipeTag;
    Optional<String> ingredientId;
    Optional<String> ingredientName;
    Optional<String> authorId;
    Optional<String> authorName;
    Optional<String> vegan;

    public RecipeSearch(){
        super();
    }

    public RecipeSearch(Http.Request request){
        name = request.queryString("name");
        dateGreater = request.queryString("greaterDate");
        dateLower = request.queryString("lesserDate");
        description = request.queryString("description");
        recipeTag = request.queryString("tag");
        ingredientId = request.queryString("ingredientId");
        ingredientName = request.queryString("ingredientName");
        authorName = request.queryString("authorName");
        authorId = request.queryString("authorId");
        vegan = request.queryString("vegan");
    }

    public boolean checkNotNulls(){
        Field[] allFields = this.getClass().getDeclaredFields();

        for (Field field : allFields) {
            String str = (String) invokeGetter(this, field.getName());
            if (str != null)
                return true;
        }
        return false;
    }

    //https://java2blog.com/invoke-getters-setters-using-reflection-java/
    public Object invokeGetter(Object obj, String variableName)
    {
        try {
            variableName = variableName.substring(0, 1).toUpperCase() + variableName.substring(1);
            Method method = obj.getClass().getDeclaredMethod("get"+variableName);
            return method.invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println("Error! " + e.getMessage());
        }

        return null;
    }

    public Long convertToLong(String res){
        if (res != null) {
            try {
                return Long.valueOf(res);
            } catch (Exception e) {
                System.err.println("Error! " + e.getMessage());
            }
        }
        return -1L;
    }

    public String getName() {
        return name.orElse(null);
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }

    public String getDescription() {
        return description.orElse(null);
    }

    public void setDescription(Optional<String> description) {
        this.description = description;
    }

    public String getDateLower() {
        return dateLower.orElse(null);
    }

    public String getDateGreater() {
        return dateGreater.orElse(null);
    }

    public String getRecipeTag() {
        return recipeTag.orElse(null);
    }

    public String getIngredientId() {
        return ingredientId.orElse(null);
    }

    public Long getIngredientIdLong() {
        String res = getIngredientId();
        return convertToLong(res);
    }

    public String getIngredientName() {
        return ingredientName.orElse(null);
    }

    public String getAuthorId() {
        return authorId.orElse(null);
    }

    public Long getAuthorIdLong() {
        String res = getAuthorId();
        return convertToLong(res);
    }

    public void setAuthorId(Optional<String> authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName.orElse(null);
    }

    public String getVegan() {
        return vegan.orElse(null);
    }
}
