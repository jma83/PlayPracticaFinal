package models;

import play.data.validation.Constraints.*;

import io.ebean.Finder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class User extends BaseModel {
    @Required
    String username;
    @Required
    @Email
    String email;
    @Required
    Timestamp birthdate;
    @Required
    Integer age;
    String country = null;
    String language = null;
    String password;
    Integer privilege = 0;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    public List<Recipe> recipeList;
    @OneToOne(cascade = CascadeType.ALL)
    public RecipeBook recipeBook;


    public static final Finder<Long,User> find = new Finder<>(User.class);

    public static List<User> findAll(){
        return find.all();
    }
    public static User findById(long id){
        return find.byId(id);
    }

    public User(){
        super();
    }

    public User(String username,String email,Timestamp birthdate, Integer age, String country, String language, Integer privilege){
        super();
        this.username = username;
        this.email = email;
        this.birthdate = birthdate;
        this.age = age;
        this.country = country;
        this.language = language;
        this.privilege = privilege;

    }

    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Timestamp birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}

