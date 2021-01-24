package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Tag extends BaseModel {
    @Required
    String tagName = "";

    @JsonIgnore
    @ManyToMany(mappedBy = "tagList")
    public List<Ingredient> ingredientList;
    @ManyToMany(mappedBy = "tagList")
    public List<Recipe> recipeList;

    public Tag(){
        super();
        setTitleXML("tag");
    }

    public Tag (String tagName,List<Ingredient> ingredientList,List<Recipe> recipeList){
        this.tagName = tagName;
        this.ingredientList = ingredientList;
        this.recipeList = recipeList;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }


    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }
}
