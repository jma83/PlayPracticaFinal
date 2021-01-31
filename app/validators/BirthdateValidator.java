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
    private static final String DATE_PATTERN = "dd/MM/yyyy";




    @Override
    public boolean isValid(Date object) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        //String elpepe = formatter.format(object);
        if (object==null || "".equals(object)) {
            System.err.println("Birthdate can't be empty or null");
            errorMessage = new F.Tuple<>("Birthdate can't be empty or null", new Object[]{object});
            return false;
        }

        if (object.toString().length() < 10) {
            System.err.println("Birthdate must follow the format: dd/MM/yyyy");
            errorMessage = new F.Tuple<>("Birthdate must follow the format: dd/MM/yyyy", new Object[]{object});
            return false;
        }
        Date d2 = new Date();
        Calendar a = getCalendar(object);
        Calendar b = getCalendar(d2);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);

        if (diff < 0){
            System.err.println("Birthdate must be greater than the current one");
            errorMessage = new F.Tuple<>("Birthdate must be greater than the current one", new Object[]{object});
            return false;
        }

        if (diff < 18){
            System.err.println("Birthdate must be greater than 18 years");
            errorMessage = new F.Tuple<>("Birthdate must be greater than 18 years", new Object[]{object});
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
        return errorMessage;
    }

    @Override
    public void initialize(Birthdate constraintAnnotation) {
        System.out.println(constraintAnnotation.message());
    }
}