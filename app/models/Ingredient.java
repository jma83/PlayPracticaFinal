package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import play.data.validation.Constraints.*;
import validators.Description;
import validators.Name;
import validators.Price;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Ingredient extends BaseModel {

    public static final Finder<Long,Ingredient> find = new Finder<>(Ingredient.class);

    public static List<Ingredient> findAll(){
        return find.all();
    }
    public static Ingredient findById(long id){
        return find.byId(id);
    }
    public static List<Ingredient> findByName(String name){
        return find.query().where().eq("name", name).findList();
    }
    public static List<Ingredient> findByNameAndRecipeId(String name, Long idRecipe){
        return find.query().where().eq("name", name).in("recipeList.id", idRecipe).findList();
    }
    public static List<Ingredient> findByRecipeId(Long idRecipe){
        return find.query().where().in("recipeList.id",idRecipe).findList();
    }
    public static Ingredient findByIdAndRecipeId(Long id, Long idRecipe){
        return find.query().where().eq("id", id).in("recipeList.id",idRecipe).findOne();
    }

    public static List<Ingredient> findAndMergeIngredientList(List<Ingredient> ingredientList){
        Set<String> listNames = new HashSet<>();
        for (Ingredient i:ingredientList) {
            listNames.add(i.getName());
        }
        List<Ingredient> ingredientList2 = find.query().where().in("name", listNames).findList();

        for (int i = 0; i< ingredientList.size();i++) {
            for (Ingredient ingredient : ingredientList2) {
                if (ingredientList.get(i) != null && ingredient != null)
                    if (ingredientList.get(i).getName().equals(ingredient.getName())) {
                        ingredientList.set(i, ingredient);
                        break;
                    }
            }
        }
        //https://www.baeldung.com/java-remove-duplicates-from-list
        ingredientList = new ArrayList<>(new HashSet<>(ingredientList));
        return ingredientList;
    }


    @Required
    @Name
    String name;
    @Required
    @Description
    String description;
    @Price
    Float price;
    String coin;

    @JsonIgnore
    @ManyToMany(mappedBy = "ingredientList")
    public List<Recipe> recipeList;


    public Ingredient (){
        super();
        setTitleXML("ingredient");
    }

    public Ingredient (String name, String description, Float price, String coin, List<Tag> tagList,List<Recipe> recipeList){
        super();
        this.name = name;
        this.description = description;
        this.price = price;
        this.coin = coin;
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public void setRecipeList(ArrayList<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public void update(Ingredient ingredient){
        if (ingredient!=null) {
            this.name = ingredient.getName();
            this.description = ingredient.getDescription();
            this.price = ingredient.getPrice();
            this.coin = ingredient.getCoin();
        }
    }
}
