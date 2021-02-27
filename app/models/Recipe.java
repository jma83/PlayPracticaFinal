package models;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.ExpressionList;
import io.ebean.Finder;
import org.checkerframework.common.value.qual.BoolVal;
import play.data.validation.Constraints.*;
import utils.RecipeSearch;
import validators.Description;
import validators.Name;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import java.text.SimpleDateFormat;

@Entity
public class Recipe extends BaseModel {

    public static final Finder<Long,Recipe> find = new Finder<>(Recipe.class);

    public static List<Recipe> findAll(){
        return find.all();
    }
    public static Recipe findById(long id){
        return find.byId(id);
    }
    public static List<Recipe> findByIngredient(Ingredient i, Long idRecipe){
        return find.query().where().in("ingredientList", i).eq("id",idRecipe).findList();
    }
    public static Recipe findByIngredientId(Long id, Long idRecipe){
        return find.query().where().in("ingredientList.id", id).eq("id",idRecipe).findOne();
    }
    public static List<Recipe> findByNameAndUser(String name, User u, Long id){
        return find.query().where().eq("name", name).in("author", u).ne("id",id).findList();
    }
    public static List<Recipe> findByRecipeBookId(Long id){
        return find.query().where().eq("recipeBookList.id", id).findList();
    }
    public static Recipe findByIdAndRecipeBookId(Long id, Long id2){
        return find.query().where().eq("id", id).eq("recipeBookList.id", id2).findOne();
    }


    public static List<Recipe> findByFilter(RecipeSearch recipeSearch){
        Date date1 = null;
        Date date2 = null;
        if (recipeSearch.getDateGreater() != null)
            date1 = Recipe.toDate(recipeSearch.getDateGreater());
        if (recipeSearch.getDateLower() != null)
            date2 = Recipe.toDate(recipeSearch.getDateLower());

        ExpressionList<Recipe> recipeQuery = find.query().where();
        if (recipeSearch.getName()!=null)
            recipeQuery = recipeQuery.like("name", recipeSearch.getName()+"%");
        if (recipeSearch.getDescription()!=null)
            recipeQuery = recipeQuery.like("description", recipeSearch.getDescription()+"%")  ;
        if (recipeSearch.getRecipeTag()!=null)
            recipeQuery = recipeQuery.in("tagList.tagName", recipeSearch.getRecipeTag());
        if (date1!=null)
            recipeQuery = recipeQuery.ge("whenCreated", date1);
        if (date2!=null)
            recipeQuery = recipeQuery.le("whenCreated", date2);
        if (recipeSearch.getIngredientId()!=null)
            recipeQuery = recipeQuery.in("ingredientList.id", recipeSearch.getIngredientIdLong());
        if (recipeSearch.getIngredientName()!=null)
            recipeQuery = recipeQuery.in("ingredientList.name", recipeSearch.getIngredientName());
        if (recipeSearch.getIngredientTag()!=null)
            recipeQuery = recipeQuery.in("ingredientList.tagList.tagName", recipeSearch.getIngredientTag());
        if (recipeSearch.getAuthorId()!=null)
            recipeQuery = recipeQuery.in("author.id", recipeSearch.getAuthorIdLong());
        if (recipeSearch.getAuthorName()!=null)
            recipeQuery = recipeQuery.in("author.username", recipeSearch.getAuthorName());

        return recipeQuery.findList();
    }

    @Required
    @Name
    String name;
    @Required
    @Description
    String description;
    @BoolVal({true, false})
    Boolean visibility = true;
    @ManyToMany(cascade = CascadeType.ALL)
    @Valid
    List<Tag> tagList = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @Valid
    public List<Ingredient> ingredientList = new ArrayList<>();
    @ManyToOne
    public User author;
    @JsonIgnore
    @ManyToMany(mappedBy = "recipeList")
    public List<RecipeBook> recipeBookList = new ArrayList<>();


    public Recipe (){
        super();
        setTitleXML("recipe");
    }

    public Recipe (String name, String description, Boolean visibility, List<Tag> tagList,
                   List<Ingredient> ingredientList,User author){
        super();
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.tagList = tagList;
        this.ingredientList = ingredientList;
        this.author = author;
    }

    public void update(Recipe recipe){
        this.name = recipe.getName();
        this.description = recipe.getDescription();
        this.visibility = recipe.getVisibility();
        this.tagList = recipe.getTagList();
        this.ingredientList = recipe.getIngredientList();
        if (recipe.getAuthor()!=null)
            this.author = recipe.getAuthor();
        else
            this.author = this.getAuthor();
    }

    public static Date toDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date ts = dateFormat.parse(date);
            return ts;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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


}
