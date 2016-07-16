package com.gracelogic.platform.web.filter;

import java.util.Locale;

public class LocaleHolder {
    private static ThreadLocal<Locale> locale = new ThreadLocal<Locale>();

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale l) {
        locale.set(l);
    }

    public static void setLocale(String l) {
        locale.set(Locale.forLanguageTag(l));
    }
}
