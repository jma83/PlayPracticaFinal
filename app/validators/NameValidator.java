package validators;

import play.data.validation.Constraints;
import play.libs.F;
import utils.MessageUtils;
import utils.StringUtils;

import javax.validation.ConstraintValidator;

public class NameValidator extends Constraints.Validator<String> implements ConstraintValidator<Name,String> {

    @Override
    public boolean isValid(String object) {
        return false;
    }

    @Override
    public boolean isValid(String object, javax.validation.ConstraintValidatorContext constraintContext) {
        if (object==null || "".equals(object)) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.nameNull).addConstraintViolation();
            return false;
        }

        if (object.length() < 2 || object.length() > 30) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.nameLength).addConstraintViolation();
            return false;
        }
        if (StringUtils.checkNameFormat(object)){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.nameFormat).addConstraintViolation();
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return null;
    }
}