package controllers;

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

//FormFactory (solo post y get)
public class UserController {
    @Inject
    FormFactory formFactory;

    List<User> users = new ArrayList<>();

    public Result createUser(Http.Request request){
        Form<User> form = formFactory.form(User.class).bindFromRequest(request);
        User usu = form.get();
        Result res = null;
        //Map<String,String> map = form.rawData();

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }


        //insertar usuario
        this.users.add(usu);

        System.out.println("Users: " + this.users);
        if (res==null)
        res = this.contentNegotiation(request,this.users);

        return res.withHeader("X-User-Count",String.valueOf(users.size()));
    }

    public Result getUser(Http.Request request){
        Result res = null;

        if (users.size() == 0) {
            res = Results.notFound("Sin resultados!");
        }else {

            Optional<String> index = request.queryString("index");

            if (index.isPresent()) {
                System.out.println(index.get());
                try {
                    res = this.getConIndex(Integer.parseInt(index.get()),request);
                } catch (NumberFormatException e) {
                    System.err.println("Error formato no numerico");
                    res = Results.badRequest("Error formato no numerico");

                }
            }
            if (res == null)
            res = this.contentNegotiation(request,this.users);

        }


        return res.withHeader("X-User-Count",String.valueOf(users.size()));

    }

    public Result getConIndex(int in,Http.Request request){
        Result res = null;
        if (in < 0 || users.size() <= in){
            res = Results.notFound("GET - Sin resultados");
        }else if (users.get(in) == null) {
            res = Results.notFound("GET - Sin resultados");
        }

        if (res == null) {
            List<User> users = new ArrayList<>();
            users.add(this.users.get(in));
            res = this.contentNegotiation(request,users);
        }

        return res;
    }

    public Result contentNegotiation(Http.Request request,List<User> users){
        Result res = null;
        if (request.accepts("application/xml")){
            Content content = views.xml.User.users.render(users);
            res = Results.ok(content);
        }else if (request.accepts("application/json")) {
            res = Results.ok(Json.toJson(users));
        }else{
            res = Results.badRequest();
        }

        return res;
    }
}
