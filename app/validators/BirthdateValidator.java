package validators;

import play.data.validation.Constraints;
import play.libs.F;
import utils.DateUtils;
import utils.MessageUtils;

import javax.validation.ConstraintValidator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BirthdateValidator extends Constraints.Validator<Date> implements ConstraintValidator<Birthdate,Date> {
    F.Tuple<String, Object[]> errorMessage;

    @Override
    public boolean isValid(Date object) {
        return false;
    }

    @Override
    public boolean isValid(Date object, javax.validation.ConstraintValidatorContext constraintContext) {

        if (object==null || "".equals(object)) {
            System.err.println(object);
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.birthdateNull).addConstraintViolation();
            return false;
        }

        if (object.toString().length() < 10) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.birthdateFormat).addConstraintViolation();
            return false;
        }
        Date d2 = new Date();
        int diff = DateUtils.yearsBetweenDates(object, d2);

        if (diff < 0){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.birthdatePositiveRange).addConstraintViolation();
            return false;
        }

        if (diff < 16){
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(MessageUtils.birthdateAgeRange).addConstraintViolation();
            return false;
        }

        return true;
    }


    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return null;
    }

    @Override
    public void initialize(Birthdate constraintAnnotation) {
        System.out.println(constraintAnnotation.message());
    }
}