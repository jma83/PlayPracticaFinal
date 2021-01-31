package validators;

import play.data.validation.Constraints.*;
import play.libs.F;

import javax.validation.ConstraintValidator;

public class PasswordValidator extends Validator<String> implements ConstraintValidator<Password,String> {

    F.Tuple<String, Object[]> errorMessage;

    @Override
    public boolean isValid(String object) {
        if (object==null || "".equals(object)) {
            errorMessage = new F.Tuple<>("Password can't be empty or null", new Object[]{object});
            return false;
        }
        if (object.contains(" ")){
            errorMessage = new F.Tuple<>("Password can't contain whitespaces", new Object[]{object});
            return false;
        }
        if (object.length() < 6 || object.length() > 15) {
            errorMessage = new F.Tuple<>("Password must be between 6 and 15 characters", new Object[]{object});
            return false;
        }
        if (!StringUtils.isAlphanumeric(object)){
            errorMessage = new F.Tuple<>("Password must only have alphanumeric characters", new Object[]{object});
            return false;
        }
        if (!StringUtils.checkPasswordFormat(object)){
            errorMessage = new F.Tuple<>("Password must contain at least one upper case, one lower case and one number", new Object[]{object});
            return false;
        }
        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return errorMessage;
    }
}
