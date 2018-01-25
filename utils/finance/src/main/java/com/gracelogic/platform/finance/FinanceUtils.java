package com.gracelogic.platform.finance;

import org.apache.commons.lang3.StringUtils;

public class FinanceUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Long toDecimal(Double value) {
        if (value == null) {
            return null;
        }

        return (long) (round(value, 4) * 10000);
    }

    public static Double toFractional(Long value) {
        if (value == null) {
            return null;
        }
        return (double) value / 10000;
    }

    public static double toFractional2Rounded(long value) {
        return round(toFractional(value), 2);
    }

    public static Long stringToLong(String str) {
        double la = 0;
        if (!StringUtils.isEmpty(str)) {
            str = str.replace(',', '.');
            la = Double.parseDouble(str);
        }
        return FinanceUtils.toDecimal(la);
    }

}

