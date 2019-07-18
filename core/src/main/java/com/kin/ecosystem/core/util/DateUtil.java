package com.kin.ecosystem.core.util;

import android.annotation.SuppressLint;
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

    @SuppressLint("SimpleDateFormat")
    private static DateFormat utcDateFormatExtended = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static {
		utcDateFormat.setTimeZone(utcTimeZone);
        utcDateFormatExtended.setTimeZone(utcTimeZone);
	}

    public static String getDateFormatted(String dateStr) {
        Date date = getDateFromUTCString(dateStr);
        if(date != null){
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static Date getDateFromUTCString(String dateStr) {
        try {
            return utcDateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getCurrentUtcDate() {
        return utcDateFormatExtended.format(new Date());
    }
}
