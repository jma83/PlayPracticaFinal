package validators;
import play.data.validation.Constraints.*;
import play.libs.F;

import javax.validation.ConstraintValidator;

public class UsernameValidator extends Validator<String> implements ConstraintValidator<Username,String> {
    F.Tuple<String, Object[]> errorMessage;
    @Override
    public boolean isValid(String object) {
        if (object==null || "".equals(object)) {
            errorMessage = new F.Tuple<>("Username can't be empty or null", new Object[]{object});
            return false;
        }
        if (object.contains(" ")){
            errorMessage = new F.Tuple<>("Username can't contain whitespaces", new Object[]{object});
            return false;
        }
        if (object.length() < 2 || object.length() > 20) {
            errorMessage = new F.Tuple<>("Username must be between 2 and 20 characters", new Object[]{object});
            return false;
        }
        if (!StringUtils.isAlphanumeric(object)){
            errorMessage = new F.Tuple<>("Username must only have alphanumeric characters", new Object[]{object});
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return errorMessage;
    }


}
