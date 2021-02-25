package models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import play.data.format.Formats;
import play.data.validation.Constraints.*;

import io.ebean.Finder;
import validators.Birthdate;
import validators.Username;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Entity
@JsonFilter("userTokenFilter")
public class User extends BaseModel {
    @Required
    @Username
    String username;

    @Required
    @Email
    String email;

    @Birthdate
    @Formats.DateTime(pattern = "yyyy-MM-dd")
    Date birthdate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer age;
    String country;
    String language;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    @JsonIgnore
    public List<Recipe> recipeList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    public RecipeBook recipeBook;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public UserToken userToken;

    @JsonIgnore
    public static final Finder<Long,User> find = new Finder<>(User.class);
    public static List<User> findAll(){
        return find.all();
    }
    public static User findById(long id){
        return find.byId(id);
    }
    public static User findByToken(UserToken t){
        return find.query().where().in("userToken", t).findOne();
    }
    public static List<User> findUsername(String user){
        return find.query().where().eq("username", user).findList();
    }
    public static List<User> findUsernameId(String user, Long id){
        return find.query().where().eq("username", user).ne("id",id).findList();
    }


    public User(){
        super();
        setTitleXML("user");
    }

    public User(String username,String email,Date birthdate, Integer age, String country, String language){
        super();
        this.username = username;
        this.email = email;
        this.birthdate = birthdate;
        this.age = age;
        this.country = country;
        this.language = language;
        this.userToken = new UserToken(username);
    }

    public void setAge() {
        //https://www.baeldung.com/java-date-difference
        if (this.birthdate != null) {
            Date date2 = new Date();
            long diffInMillies = Math.abs(this.birthdate.getTime() - date2.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            this.age = Math.toIntExact(diff / 365);
        }

    }

    public void setUserToken(){
        this.userToken = new UserToken(this.username);
    }

    public void setRecipeBook(){
        this.recipeBook = new RecipeBook
                (this.getUsername() + "'s recipe book",
                "My recipe book",
                new ArrayList<>(),
                this);
    }

    public void init() {
        setAge();
        setUserToken();
        setRecipeBook();
    }

    public void update(User u){
        this.setBirthdate(u.getBirthdate());
        this.setCountry(u.getCountry());
        this.setEmail(u.getEmail());
        this.setLanguage(u.getLanguage());
        this.setUsername(u.getUsername());
        this.setAge();
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

    public RecipeBook getRecipeBook() {
        return recipeBook;
    }

    public void setRecipeBook(RecipeBook recipeBook) { this.recipeBook = recipeBook; }

    public UserToken getUserToken() {
        return userToken;
    }

    public void setUserToken(UserToken userToken) {
        this.userToken = userToken;
    }

}

