package validators;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidator;

public class DescriptionValidator extends Constraints.Validator<String> implements ConstraintValidator<Description,String> {
    F.Tuple<String, Object[]> errorMessage;

    @Override
    public boolean isValid(String object) {
        if (object==null || "".equals(object)) {
            errorMessage = new F.Tuple<>("Description can't be empty or null", new Object[]{object});
            return false;
        }

        if (object.length() < 2 || object.length() > 150) {
            errorMessage = new F.Tuple<>("Description must be between 2 and 150 characters", new Object[]{object});
            return false;
        }
        if (!StringUtils.checkNameFormat(object)){
            errorMessage = new F.Tuple<>("Invalid description format", new Object[]{object});
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return errorMessage;
    }

}