package com.gracelogic.platform.payment;

import org.apache.commons.codec.binary.Base64;

public class Utils {
    public static String getBase64Authorization(String clientId, String password) {
        String temp = clientId + ":" + password;
        return Base64.encodeBase64String(temp.getBytes());
    }
}
