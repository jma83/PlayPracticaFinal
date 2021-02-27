package utils;

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
    Optional<String> ingredientTag;
    Optional<String> ingredientName;
    Optional<String> authorId;
    Optional<String> authorName;

    public RecipeSearch(){
        super();
    }

    public RecipeSearch(Http.Request request){
        name = request.queryString("name");
        dateLower = request.queryString("greaterDate");
        dateGreater = request.queryString("lesserDate");
        description = request.queryString("description");
        recipeTag = request.queryString("tag");
        ingredientId = request.queryString("ingredientId");
        ingredientName = request.queryString("ingredientName");
        ingredientTag = request.queryString("ingredientTag");
        authorName = request.queryString("authorName");
        authorId = request.queryString("authorId");
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

    public String getName() {
        if (name.isPresent()){
            return name.get();
        }
        return null;
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }

    public String getDescription() {
        if (description.isPresent()){
            return description.get();
        }
        return null;
    }

    public void setDescription(Optional<String> description) {
        this.description = description;
    }

    public String getDateLower() {
        if (dateLower.isPresent()){
            return dateLower.get();
        }
        return null;
    }

    public void setDateLower(Optional<String> dateLower) {
        this.dateLower = dateLower;
    }

    public String getDateGreater() {
        if (dateGreater.isPresent()){
            return dateGreater.get();
        }
        return null;
    }

    public void setDateGreater(Optional<String> dateGreater) {
        this.dateGreater = dateGreater;
    }

    public String getRecipeTag() {
        if (recipeTag.isPresent()){
            return recipeTag.get();
        }
        return null;
    }

    public void setRecipeTag(Optional<String> recipeTag) {
        this.recipeTag = recipeTag;
    }

    public String getIngredientId() {
        if (ingredientId.isPresent()){
            return ingredientId.get();
        }
        return null;
    }

    public Long getIngredientIdLong() {
        String res = getIngredientId();
        return convertToLong(res);
    }

    public void setIngredientId(Optional<String> ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientTag() {
        if (ingredientTag.isPresent()){
            return ingredientTag.get();
        }
        return null;
    }

    public void setIngredientTag(Optional<String> ingredientTag) {
        this.ingredientTag = ingredientTag;
    }

    public String getIngredientName() {
        if (ingredientName.isPresent()){
            return ingredientName.get();
        }
        return null;
    }

    public void setIngredientName(Optional<String> ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getAuthorId() {
        if (authorId.isPresent()){
            return authorId.get();
        }
        return null;
    }

    public Long getAuthorIdLong() {
        String res = getAuthorId();
        return convertToLong(res);
    }

    public void setAuthorId(Optional<String> authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        if (authorName.isPresent()){
            return authorName.get();
        }
        return null;
    }

    public void setAuthorName(Optional<String> authorName) {
        this.authorName = authorName;
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
}
