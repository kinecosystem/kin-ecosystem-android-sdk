package kin.ecosystem.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    private static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.US);
    private static DateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
    private static TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    public static String getDateFormatted(String dateStr) {
        Date date = getDateFromUTCString(dateStr);
        if(date != null){
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static Date getDateFromUTCString(String dateStr) {
        utcDateFormat.setTimeZone(utcTimeZone);
        try {
            return utcDateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
}
