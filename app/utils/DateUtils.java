package utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //https://www.baeldung.com/java-date-difference
    public static int yearsBetweenDates(Date date1, Date date2){
        long diffInMillies = Math.abs(date1.getTime() - date2.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        int result = Math.toIntExact(diff / 365);
        return result;
    }

}
