package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import play.data.validation.Constraints.*;
import validators.Description;
import validators.Name;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RecipeBook extends BaseModel{
    public static final Finder<Long,RecipeBook> find = new Finder<>(RecipeBook.class);

    public static List<RecipeBook> findAll(){
        return find.all();
    }
    public static RecipeBook findById(long id){
        return find.byId(id);
    }
    public static RecipeBook findByRecipe(long id, Recipe recipe){
        return find.query().where().eq("id",id).in("recipeList",recipe).findOne();
    }
    public static List<RecipeBook> findByRecipeName(long id, String recipe){
        return find.query().where().eq("id",id).eq("recipeList.name",recipe).findList();
    }


    @Required
    @Name
    String name;
    @Required
    @Description
    String review;
    @ManyToMany(cascade = CascadeType.ALL)
    public List<Recipe> recipeList = new ArrayList<>();
    @OneToOne(mappedBy = "recipeBook")
    public User author;

    public RecipeBook (){
        super();
        setTitleXML("recipeBook");
    }

    public RecipeBook (String name, String review, List<Recipe> recipeList, User author){
        this.name = name;
        this.review = review;
        this.recipeList = recipeList;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void update(RecipeBook rb){
        if (rb.getName() != null)
        this.name = rb.getName();
        if (rb.getReview() != null)
        this.review = rb.getReview();
        if (rb.getAuthor() != null)
        this.author = rb.getAuthor();
        if (rb.getRecipeList() != null)
        this.recipeList = rb.getRecipeList();
    }

    public void reset(){
        this.name = "";
        this.review = "";
        this.author = null;
        this.recipeList = new ArrayList<>();
    }
}
