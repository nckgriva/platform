package com.gracelogic.platform.web.dto;

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
