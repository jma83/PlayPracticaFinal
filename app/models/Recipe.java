package models;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import play.data.validation.Constraints.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

@Entity
public class Recipe extends BaseModel {

    public static final Finder<Long,Recipe> find = new Finder<>(Recipe.class);

    public static List<Recipe> findAll(){
        return find.all();
    }
    public static Recipe findById(long id){
        return find.byId(id);
    }

    @Required
    String name;
    @Required
    String description;
    Boolean visibility = true;
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @Valid
    List<Tag> tagList = new ArrayList<>();
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @Valid
    public List<Ingredient> ingredientList = new ArrayList<>();
    @JsonIgnore
    @ManyToOne
    public User author = null;
    @JsonIgnore
    @ManyToMany(mappedBy = "recipeList")
    public List<RecipeBook> recipeBookList = new ArrayList<>();


    public Recipe (){
        super();
        setTitleXML("recipe");
    }

    public Recipe (String name, String description, Boolean visibility, List<Tag> tagList,
                   List<Ingredient> ingredientList,User author,List<RecipeBook> recipeBookList){
        super();
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.tagList = tagList;
        this.ingredientList = ingredientList;
        this.author = author;
        this.recipeBookList = recipeBookList;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean publicRecipe) {
        this.visibility = publicRecipe;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public void setTagList(ArrayList<Tag> tagList) {
        this.tagList = tagList;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public void setIngredientList(ArrayList<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public List<RecipeBook> getRecipeBookList() {
        return recipeBookList;
    }

    public void setRecipeBookList(List<RecipeBook> recipeBookList) {
        this.recipeBookList = recipeBookList;
    }

    public void update(Recipe recipe){
        this.name = recipe.getName();
        this.description = recipe.getDescription();
        this.author = recipe.getAuthor();
        this.visibility = recipe.getVisibility();
        this.tagList = recipe.getTagList();
    }
}
