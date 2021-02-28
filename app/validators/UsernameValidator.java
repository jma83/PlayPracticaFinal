package validators;
import play.data.validation.Constraints.*;
import play.libs.F;
import utils.MessageUtils;
import utils.StringUtils;

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
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.usernameNull).addConstraintViolation();

            return false;
        }
        if (object.contains(" ")){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.usernameWhitespaces).addConstraintViolation();
            return false;
        }
        if (object.length() < 3 || object.length() > 20) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.usernameLength).addConstraintViolation();
            return false;
        }
        if (!StringUtils.isAlphanumeric(object)){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.usernameAlphanumeric).addConstraintViolation();
            return false;
        }
        if (!StringUtils.usernameFormat(object)){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.usernameFormat).addConstraintViolation();
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return errorMessage;
    }


}
