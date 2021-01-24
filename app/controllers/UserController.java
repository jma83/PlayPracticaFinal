package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ebeaninternal.server.lib.util.Str;
import models.User;
import models.User2.UserModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Content;
import scala.Int;

import javax.inject.Inject;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserController extends BaseController {
    @Inject
    FormFactory formFactory;

    List<User> users = new ArrayList<>();
    String noResults = "Sin resultados!";
    String formatError = "Error formato no numerico";
    String headerCount = "X-User-Count";

    public Result createUser(Http.Request request){
        Result res = null;
        Form<User> form = null;
        User user = new User();

        Document doc = request.body().asXml();
        JsonNode json = request.body().asJson();

        if (doc != null){
            User u = (User) createWithXML(doc,user,this).get(0);
            form = formFactory.form(User.class).fill(u);
        }else if (json != null){
            form = formFactory.form(User.class).bindFromRequest(request);
        }else{
            res = Results.badRequest(noResults);
        }

        user = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }

        user.save();
        users.add(user);
        System.out.println("User inserted: " + user);


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
        User user = form.get();
        if (form.hasErrors()){
            System.err.println(form.errorsAsJson());
            res = Results.badRequest(form.errorsAsJson());
        }
        if (user != null && res == null && index.isPresent()){
            Long id = Long.valueOf(index.get());
            User userUpdate = User.findById(id);
            userUpdate.updateUser(user);
            this.users.add(userUpdate);
            userUpdate.update();
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
    public void iterateElementList(){
        System.out.println("iterateElementList CHILD!");
    }
}
