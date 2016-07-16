package com.gracelogic.platform.web.dto;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 22:38
 */
public class ValueRequest extends PlatformRequest {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ValueRequest{" +
                "value='" + value + '\'' +
                '}';
    }
}
