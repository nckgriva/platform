package com.gracelogic.platform.feedback.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtils {
    public static final ThreadLocal<DateFormat> DEFAULT_FORMAT_WITH_MILLISECONDS = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        }
    };


    public static final ThreadLocal<DateFormat> DEFAULT_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }
    };

    public static final ThreadLocal<DateFormat> SHORT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yyyy");
        }

    };

    public static final ThreadLocal<DateFormat> WIALON_IPS1_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("ddMMyyHHmmss");
        }

    };

    public static final ThreadLocal<DateFormat> DATE_WITH_TIME_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        }

    };
    public static final ThreadLocal<DateFormat> DATE_WITHOUT_SECONDS_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        }

    };

    public static final ThreadLocal<DateFormat> INTELLECT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yy");
        }
    };

    public static final ThreadLocal<DateFormat> PASSPORT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };


    public static final ThreadLocal<DateFormat> INTELLECT_TIME_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }

    };


    public static Date getDayBegin(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
        calendar.set(GregorianCalendar.HOUR, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String getTimeRest(long mills) {
        long days = mills / IN_DAY;
        long hours = (mills - days * IN_DAY) / IN_HOUR;
        long minutes = (mills - days * IN_DAY - hours * IN_HOUR) / IN_MINUTE;

        return String.format("%d д. %d ч. %d м.", days, hours, minutes);
    }

    public static final long IN_MINUTE = 1000 * 60;
    public static final long IN_HOUR = IN_MINUTE * 60;
    public static final long IN_DAY = IN_HOUR * 24;

    public static Date formatStringToDate(String str) {
        try {
            return SHORT_DATE_FORMAT.get().parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date formatStringToDateWithTime(String str) {
        try {
            return DATE_WITH_TIME_FORMAT.get().parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDateWithTimeToString(Date date) {
        try{
            return DATE_WITH_TIME_FORMAT.get().format(date);
        }
        catch (Exception e) {
            return "";
        }
    }
}
