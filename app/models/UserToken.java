package models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.ExpressionList;
import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;


@Entity
public class UserToken extends BaseModel{

    @JsonIgnore
    public static final Finder<Long,UserToken> find = new Finder<>(UserToken.class);
    public static List<UserToken> findUserToken(String token){
        return find.query().where().eq("token", token).findList();
    }


    @OneToOne(mappedBy="userToken")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String token;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Boolean visible = false;

    public UserToken(){
        super();
        this.titleXML="userToken";
    }

    public UserToken(String username){
        //https://stackoverflow.com/questions/17141292/oauth-2-0-generating-token-and-secret-token
        //https://www.javatpoint.com/java-get-current-date

        token = this.generateUsernameToken(username);
    }

    public String generateUsernameToken(String username){
        this.titleXML="userToken";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String currentdate = dtf.format(now);
        String keySource = username + currentdate + Math.random();
        byte[] bytes = Base64.getEncoder().encode(keySource.getBytes());
        return new String(bytes);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

}
