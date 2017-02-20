package com.gracelogic.platform.web.service;

import org.apache.commons.lang3.LocaleUtils;

import java.util.Locale;

public class LocaleHolder {
    private static ThreadLocal<Locale> locale = new ThreadLocal<Locale>();

    public static Locale getLocale() {
        if (locale.get() == null) {
            return Locale.getDefault();
        }
        else {
            return locale.get();
        }
    }

    public static void setLocale(Locale l) {
        locale.set(l);
    }

    public static void setLocale(String l) {
        try {
            locale.set(LocaleUtils.toLocale(l));
        }
        catch (Exception ignored) {}
    }
}
