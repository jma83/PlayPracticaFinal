package actionCompostionAuth;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.src.JSONManager;
import models.UserToken;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security.*;
import utils.MessageUtils;

import java.util.List;
import java.util.Optional;

public class UserAuthenticator extends Authenticator {
    private static final String AUTH = "Auth";
    private static final String Bearer = "Bearer ";
    private static final String errorToken = "Error. Invalid user identification. The '"+AUTH+"' token is incorrect";
    private static final String errorHeader = "Error. You need to be identified with '"+AUTH+"' header token, before sending this request";
    private static String errorName = "";

    public UserAuthenticator() {
        super();
    }

    @Override
    public Optional<String> getUsername(Http.Request req) {
        UserToken str = validateUserToken(req);
        if (str==null) return Optional.empty();

        return Optional.of(str.getToken());

    }

    @Override
    public Result onUnauthorized(Http.Request req) {
        return JSONManager.genericJsonResponse(false,errorName,400);
    }


    public static UserToken validateUserToken(Http.Request req){
        if (req.hasHeader(AUTH)){
            errorName = errorToken;
            Optional<String> userToken = req.header(AUTH);
            if (userToken.isPresent()){
                String[] realToken = userToken.get().split(Bearer);
                if (realToken.length >= 2) {
                    List<UserToken> list = UserToken.findUserToken(realToken[1]);
                    if (list.size() == 1){
                        return list.get(0);
                    }
                }

            }
        }else{
            errorName = errorHeader;
        }
        return null;
    }

}

