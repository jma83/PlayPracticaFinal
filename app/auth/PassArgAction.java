package auth;

import models.User;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;
//https://stackoverflow.com/questions/55194820/play-framework-2-7-authorization-get-user
//https://www.playframework.com/documentation/2.7.x/JavaActionsComposition#Passing-objects-from-action-to-controller
public class PassArgAction extends play.mvc.Action.Simple {
    public CompletionStage<Result> call(Http.Request req) {
        return delegate.call(req.addAttr(Attrs.USER, User.findByToken(UserAuthenticator.validateUserToken(req))));
    }
}

