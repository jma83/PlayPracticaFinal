package auth;

import akka.http.javadsl.model.headers.Authorization;
import akka.http.javadsl.model.headers.HttpCredentials;
import akka.http.javadsl.model.headers.OAuth2BearerToken;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.routes;
import io.ebean.ExpressionList;
import io.ebeaninternal.server.lib.util.Str;
import models.UserToken;
import play.libs.Json;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security.*;

import java.util.List;
import java.util.Optional;

public class UserAuthenticator extends Authenticator {
    private static final String AUTH = "Auth";
    private static final String Bearer = "Bearer ";
    private static final String errorToken = "The '"+AUTH+"' token is incorrect";
    private static final String errorHeader = "You need to be identified with '"+AUTH+"' header token, before sending this request";
    private static String errorName = "";

    public UserAuthenticator() {
        super();
    }

    @Override
    public Optional<String> getUsername(Http.Request req) {
         if (req.hasHeader(AUTH)){
             errorName = errorToken;
            Optional<String> userToken = req.header(AUTH);
            if (userToken.isPresent()){
                String fullToken = userToken.get();
                String[] realToken = fullToken.split(Bearer);
                if (realToken.length >= 2) {
                    String value = realToken[1];
                    List<UserToken> list = UserToken.findUserToken(value);
                    if (list.size() == 1){
                        return Optional.of(list.get(0).getToken());
                    }
                }

            }
        }else{
             errorName = errorHeader;
         }
        return Optional.empty();

    }

    @Override
    public Result onUnauthorized(Http.Request req) {
        ObjectNode result = Json.newObject();
        result.put("Error",errorName);
        return Results.unauthorized(result);
    }
}