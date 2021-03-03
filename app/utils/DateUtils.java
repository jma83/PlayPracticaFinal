package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static Date convertTimestamp(Date currentDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        String str = dateFormat.format(currentDate);
        try {
            Date d = dateFormat.parse(str);
            return d;
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return currentDate;
    }

    public static Date toDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT);
        try {
            Date ts = dateFormat.parse(date);
            return ts;
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
