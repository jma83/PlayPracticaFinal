package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserController {
    @Inject
    FormFactory formFactory;

    List<User> users = new ArrayList<>();
    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-User-Count";

    public Result createUser(Http.Request request){
        Form<User> form = formFactory.form(User.class).bindFromRequest(request);
        System.out.println("createUser0");

        User usu = form.get();
        Result res = null;
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        System.out.println("createUser1");
        usu.save();
        users.add(usu);
        System.out.println("createUser2");
        System.out.println("User inserted: " + usu);
        if (res==null)
        res = this.contentNegotiation(request,this.users);

        return res.withHeader(headerCount,String.valueOf(users.size()));
    }

    public Result getUser(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");

        users = User.findAll();
        if (users.size() == 0)
            res = Results.notFound(noResults);

        if (index.isPresent() && res==null)
            res = this.getIndexUser(index.get());

        if (res == null)
            res = this.contentNegotiation(request,this.users);


        return res.withHeader(headerCount,String.valueOf(users.size()));

    }

    public Result getIndexUser(String index){
        Result res = null;

        System.out.println(index);
        users.clear();
        try {
            User u = User.findById(Integer.parseInt(index));
            if (u != null) {
                users.add(u);
            }else{
                res = Results.notFound(noResults);
            }
        } catch (NumberFormatException e) {
            res = Results.badRequest(formatError);
        }

        return res;
    }

    public Result updateUser(Http.Request request){
        Result res = null;
        Form<User> form = formFactory.form(User.class).bindFromRequest(request);
        Optional<String> index = request.queryString("index");
        User usu = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        if (usu != null && res == null && index.isPresent()){
            Long id = Long.valueOf(index.get());
            User usuFinal = User.findById(id);
            usuFinal.setAge(usu.getAge());
            usuFinal.setBirthdate(usu.getBirthdate());
            usuFinal.setCountry(usu.getCountry());
            usuFinal.setEmail(usu.getEmail());
            usuFinal.setLanguage(usu.getLanguage());
            usuFinal.setPrivilege(usu.getPrivilege());
            usuFinal.setUsername(usu.getUsername());
            this.users.add(usuFinal);
            usuFinal.update();
            res = this.contentNegotiation(request,this.users);
        }


        return res.withHeader(headerCount,String.valueOf(users.size()));
    }

    public Result deleteUser(Http.Request request){
        Result res = null;
        Optional<String> index = request.queryString("index");
        if (index.isPresent()){
            Long id = Long.valueOf(index.get());
            User usuFinal = User.findById(id);
            this.users.add(usuFinal);
            usuFinal.delete();
            res = this.contentNegotiation(request,this.users);
        }


        return res.withHeader(headerCount,String.valueOf(users.size()));
    }

    public Result contentNegotiation(Http.Request request,List<User> users){
        Result res = null;
        System.out.println("createUser3");
        System.out.println(users.size());
        if (request.accepts("application/xml")){
            Content content = views.xml.User.users.render(users);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            ObjectMapper mapper = new ObjectMapper();
            try {

                String result = mapper.writeValueAsString(users);
                res = Results.ok(Json.parse(result));

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }else{
            res = Results.badRequest();
        }
        System.out.println("createUser4");
        this.users.clear();

        return res;
    }
}
