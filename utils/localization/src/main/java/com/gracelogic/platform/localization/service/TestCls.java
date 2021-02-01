package com.gracelogic.platform.localization.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TestCls {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
            format.setTimeZone(UTC);
            return format;
        }
    };

    public static void main(String ... args) {
        System.out.println(DEFAULT_DATE_FORMAT.get().format(new Date()));

        try {
            Date date = DEFAULT_DATE_FORMAT.get().parse("2021-01-29T08:10:18.904+03:00");
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
