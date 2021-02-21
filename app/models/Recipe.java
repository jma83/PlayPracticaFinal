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
    public static List<Recipe> findByName(String name){
        return find.query().where().eq("name", name).findList();
    }
    public static List<Recipe> findByIngredient(Ingredient i, Long id){
        return find.query().where().in("ingredientList", i).eq("id",id).findList();
    }
    //nameStr,descriptionStr,dateStr,tagListObj1,tagListObj2,ingredientListObj
    public static List<Recipe> findByFilter(String name,String description, String d1, String d2,List<Tag> tag,List<Ingredient> tag2,List<Ingredient> ingredientList){
        Date date1 = null;
        if (d1 != null)
            date1 = Recipe.toDate(d1);
        Date date2 = null;
        if (d2 != null)
            date2 = Recipe.toDate(d2);

        ExpressionList<Recipe> recipeQuery = find.query().where();
        if (name!=null)
            recipeQuery = recipeQuery.like("name", name+"%");
        if (description!=null)
            recipeQuery = recipeQuery.like("description", description+"%")  ;
        if (tag!=null && tag.size() > 0)
            recipeQuery = recipeQuery.in("tagList", tag);
        if (date1!=null)
            recipeQuery = recipeQuery.ge("whenCreated", date1);
        if (date2!=null)
            recipeQuery = recipeQuery.le("whenCreated", date2);
        if (tag2!=null && tag2.size() > 0)
            recipeQuery = recipeQuery.in("ingredientList", tag2);
        if (ingredientList!=null && ingredientList.size() > 0)
            recipeQuery = recipeQuery.in("ingredientList", ingredientList);

        return recipeQuery.findList();
    }

    public static List<Recipe> findByDate(String date){
        Date ts = Recipe.toDate(date);
        return find.query().where().eq("whenCreated", ts).findList();

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

    public boolean checkIngredient(Ingredient ingredient){
        Boolean check = false;
        for (Ingredient i:ingredientList) {
            if (i.getName().equals(ingredient.getName())){
                check = true;
                break;
            }
        }
        return check;
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


}
