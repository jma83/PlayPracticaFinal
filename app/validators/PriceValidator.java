package validators;

import play.data.validation.Constraints;
import play.libs.F;
import utils.MessageUtils;

import javax.validation.ConstraintValidator;

public class PriceValidator extends Constraints.Validator<Float> implements ConstraintValidator<Price,Float> {

    @Override
    public boolean isValid(Float object) {
        return false;
    }

    @Override
    public boolean isValid(Float object, javax.validation.ConstraintValidatorContext constraintContext) {

        if (object < 0) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.pricePositive).addConstraintViolation();
            return false;
        }

        return true;
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return null;
    }
}