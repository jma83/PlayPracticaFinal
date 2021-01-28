package auth;

import controllers.routes;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security.*;

import java.util.Optional;

public class UserAuthenticator extends Authenticator {

    public UserAuthenticator() {
        super();
    }

    @Override
    public Optional<String> getUsername(Http.Request req) {
        //TypedKey.create("username");
        return req.session().get("username");

    }

    @Override
    public Result onUnauthorized(Http.Request req) {
        return redirect(routes.UserController.loginUser()).
                flashing("Error",  "You need to login before sending this requests!");
    }
}