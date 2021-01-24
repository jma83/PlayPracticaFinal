package models.User2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import models.Recipe;
import models.RecipeBook;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserModel {
    String username;
    Integer age;


    public UserModel() {
        super();
    }

    public UserModel(String username, String email, Date birthdate, Integer age, String country, String language, Integer privilege) {
        super();
        this.username = username;
        this.age = age;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}