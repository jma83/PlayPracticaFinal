package actionCompostionAuth;

import models.User;
import play.libs.typedmap.TypedKey;
//https://stackoverflow.com/questions/55194820/play-framework-2-7-authorization-get-user
//https://www.playframework.com/documentation/2.7.x/JavaActionsComposition#Passing-objects-from-action-to-controller
public class Attrs {
    public static final TypedKey<User> USER = TypedKey.<User>create("user");
}
