package models;

import io.ebean.Finder;
import play.data.validation.Constraints.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Ingredient extends BaseModel {

    public static final Finder<Long,Ingredient> find = new Finder<>(Ingredient.class);

    public static List<Ingredient> findAll(){
        return find.all();
    }
    public static Ingredient findById(long id){
        return find.byId(id);
    }

    @Required
    String name;
    @Required
    String description;
    Float quantity;
    String measure;
    List<String> tagList;

    @ManyToMany(mappedBy = "ingredientList")
    public List<Recipe> recipeList;



    public Ingredient (){
        super();
    }

    public Ingredient (String name, String description, Float quantity, String measure, List<String> tagList,List<Recipe> recipeList){
        super();
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.measure = measure;
        this.tagList = tagList;
        this.recipeList = recipeList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }
}
