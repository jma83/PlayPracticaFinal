package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;
import play.data.validation.Constraints.*;

import io.ebean.Finder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class User extends BaseModel {
    @Required
    String username;
    @Required
    @Email
    String email;
    @Required
    Date birthdate;
    @Required
    Integer age;
    String country = null;
    String language = null;
    Integer privilege = 0;

    @JsonIgnore
    String password = null;


    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    public List<Recipe> recipeList = new ArrayList<>();
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    public RecipeBook recipeBook;

    @JsonIgnore
    public static final Finder<Long,User> find = new Finder<>(User.class);

    public static List<User> findAll(){
        return find.all();
    }
    public static User findById(long id){
        return find.byId(id);
    }


    public User(){
        super();
        setTitleXML("user");
    }

    public User(String username,String email,Date birthdate, Integer age, String country, String language, Integer privilege){
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

    public void setRecipeList(ArrayList<Recipe> recipeList){
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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void update(User u){
        this.setAge(u.getAge());
        this.setBirthdate(u.getBirthdate());
        this.setCountry(u.getCountry());
        this.setEmail(u.getEmail());
        this.setLanguage(u.getLanguage());
        this.setPrivilege(u.getPrivilege());
        this.setUsername(u.getUsername());
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

