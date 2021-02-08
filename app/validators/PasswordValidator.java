package validators;

import play.data.validation.Constraints.*;
import play.libs.F;

import javax.validation.ConstraintValidator;

public class PasswordValidator extends Validator<String> implements ConstraintValidator<Password,String> {


    @Override
    public boolean isValid(String object) {
        return false;
    }

    @Override
    public boolean isValid(String object, javax.validation.ConstraintValidatorContext constraintContext) {
        if (object==null || "".equals(object)) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Password can't be empty or null").addConstraintViolation();
            return false;
        }
        if (object.contains(" ")){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Password can't contain whitespaces").addConstraintViolation();
            return false;
        }
        if (object.length() < 6 || object.length() > 15) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Password must be between 6 and 15 characters").addConstraintViolation();
            return false;
        }
        if (!StringUtils.isAlphanumeric(object)){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Password must only have alphanumeric characters").addConstraintViolation();
            return false;
        }
        if (!StringUtils.checkPasswordFormat(object)){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Password must contain at least one upper case, one lower case and one number").addConstraintViolation();
            return false;
        }
        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return null;
    }
}
