package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import utils.DateUtils;

import javax.persistence.Column;
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
    @Column(updatable=false)
    String token;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(updatable=false)
    Boolean visible = false;

    public UserToken(){
        super();
        this.titleXML="userToken";
    }

    public UserToken(String username){
        //https://stackoverflow.com/questions/17141292/oauth-2-0-generating-token-and-secret-token
        this.titleXML="userToken";
        token = this.generateUsernameToken(username);
    }

    public String generateUsernameToken(String username){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DateUtils.DATETIME_FORMAT);
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
