package validators;

import play.data.validation.Constraints;
import play.libs.F;
import utils.MessageUtils;
import utils.StringUtils;

import javax.validation.ConstraintValidator;

public class DescriptionValidator extends Constraints.Validator<String> implements ConstraintValidator<Description,String> {

    @Override
    public boolean isValid(String object) {
        return false;
    }

    @Override
    public boolean isValid(String object, javax.validation.ConstraintValidatorContext constraintContext) {
        if (object==null || "".equals(object)) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.descriptionNull).addConstraintViolation();
            return false;
        }

        if (object.length() < 2 || object.length() > 150) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.descriptionLength).addConstraintViolation();
            return false;
        }
        if (!StringUtils.isAlphanumeric(object)){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.descriptionFormat).addConstraintViolation();
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return null;
    }

}