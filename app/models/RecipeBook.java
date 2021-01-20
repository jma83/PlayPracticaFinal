package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import play.data.validation.Constraints;

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

    @Constraints.Required
    String name;
    @Constraints.Required
    String review;
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    List<Recipe> recipeList = new ArrayList<>();
    @JsonIgnore
    @OneToOne(mappedBy = "recipeBook")
    public User author = null;

    public RecipeBook (){
        super();
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

    public void updateRecipeBook(RecipeBook rb){
        this.name = rb.getName();
        this.review = rb.getReview();
        this.author = rb.getAuthor();
    }
}
