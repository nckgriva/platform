package com.gracelogic.platform.web.dto;

public class SingleValueDTO extends PlatformRequest {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SingleValueDTO(String value) {
        this.value = value;
    }

    public SingleValueDTO() {
    }

    @Override
    public String toString() {
        return "SingleValueDTO{" +
                "value='" + value + '\'' +
                '}';
    }
}
