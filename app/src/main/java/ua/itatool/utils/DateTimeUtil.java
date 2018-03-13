package ua.itatool.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by djdf.crash on 10.03.2018.
 */

public class DateTimeUtil {

    public static String getDate(Long millis) {
        Locale locale = new Locale("ua","UA");
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTimeInMillis(millis);

        return new SimpleDateFormat("dd/MM/yy HH:mm:ss", locale).format(calendar.getTime());
    }
}
