package validators;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidator;

public class NameValidator extends Constraints.Validator<String> implements ConstraintValidator<Name,String> {
    F.Tuple<String, Object[]> errorMessage;

    @Override
    public boolean isValid(String object) {
        if (object==null || "".equals(object)) {
            errorMessage = new F.Tuple<>("Name can't be empty or null", new Object[]{object});
            return false;
        }

        if (object.length() < 2 || object.length() > 30) {
            errorMessage = new F.Tuple<>("Name must be between 2 and 30 characters", new Object[]{object});
            return false;
        }
        if (!StringUtils.checkNameFormat(object)){
            errorMessage = new F.Tuple<>("Invalid name format", new Object[]{object});
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return errorMessage;
    }
}