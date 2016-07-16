package com.gracelogic.platform.oauth;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 20:50
 */
public class OauthConstants {
    public static enum AuthProviderConstants {
        VK(1),
        OK(2),
        INSTAGRAM(3);

        private int value;

        AuthProviderConstants(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
