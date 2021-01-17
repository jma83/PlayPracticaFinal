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

public class UserController {
    @Inject
    FormFactory formFactory;

    List<User> users = new ArrayList<>();
    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-User-Count";

    public Result createUser(Http.Request request){
        Form<User> form = formFactory.form(User.class).bindFromRequest(request);
        User usu = form.get();
        Result res = null;

        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        usu.save();
        users.add(usu);
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

        this.users.clear();

        return res;
    }
}
