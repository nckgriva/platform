package com.gracelogic.platform.web.dto;


//@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
public class PlatformResponse {
    private Boolean success = true;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
