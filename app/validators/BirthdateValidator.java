package validators;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BirthdateValidator extends Constraints.Validator<Date> implements ConstraintValidator<Birthdate,Date> {
    F.Tuple<String, Object[]> errorMessage;
    private static final String DATE_PATTERN = "yyyy-MM-dd";


    @Override
    public boolean isValid(Date object) {
        return false;
    }

    @Override
    public boolean isValid(Date object, javax.validation.ConstraintValidatorContext constraintContext) {

        if (object==null || "".equals(object)) {
            //System.err.println("Birthdate can't be empty or null");
            System.err.println(object);
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Birthdate can't be empty or null").addConstraintViolation();
            return false;
        }

        if (object.toString().length() < 10) {
            //System.err.println("Birthdate must follow the format: yyyy-MM-dd");
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Birthdate must follow the format: yyyy-MM-dd").addConstraintViolation();
            return false;
        }
        Date d2 = new Date();
        Calendar a = getCalendar(object);
        Calendar b = getCalendar(d2);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);

        if (diff < 0){
            //System.err.println("Birthdate can't be greater than the current one");
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Birthdate must be greater than the current one").addConstraintViolation();
            return false;
        }

        if (diff < 18){
            //System.err.println("Birthdate must be greater or equal to 18 years");
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("Birthdate must be greater than 18 years").addConstraintViolation();
            return false;
        }

        return true;
    }
    //https://stackoverflow.com/questions/7906301/how-can-i-find-the-number-of-years-between-two-dates
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.UK);
        cal.setTime(date);
        return cal;
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