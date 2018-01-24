package com.gracelogic.platform.db.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFormatConstants {
    public static final ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        }
    };
}
