package validators;
import play.data.validation.Constraints.*;
import play.libs.F;

import javax.validation.ConstraintValidator;

public class UsernameValidator extends Validator<String> implements ConstraintValidator<Username,String> {
    F.Tuple<String, Object[]> errorMessage;

    @Override
    public boolean isValid(String object) {
        return false;
    }

    @Override
    public boolean isValid(String object, javax.validation.ConstraintValidatorContext constraintContext) {
        if (object==null || "".equals(object)) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Username can't be empty or null").addConstraintViolation();

            return false;
        }
        if (object.contains(" ")){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Username can't contain whitespaces").addConstraintViolation();
            return false;
        }
        if (object.length() < 2 || object.length() > 20) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Username must be between 2 and 20 characters").addConstraintViolation();
            return false;
        }
        if (!StringUtils.isAlphanumeric(object)){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Username must only have alphanumeric characters").addConstraintViolation();
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return errorMessage;
    }


}
