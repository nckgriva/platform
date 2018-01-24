package com.gracelogic.platform.web.dto;


//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PlatformResponse {
    private Boolean success = true;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
