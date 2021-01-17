package models;

import play.data.validation.Constraints;

import java.util.ArrayList;

public class User {
    static public ArrayList<String> nicks = new ArrayList<String>();
    static public ArrayList<Integer> ages = new ArrayList<Integer>();
    @Constraints.Required
    String nick;
    @Constraints.Required
    Integer age;

    public User(){
        super();
    }

    public User(String nick, Integer age){
        super();
        this.nick = nick;
        this.age = age;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String name) {
        this.nick = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


}

