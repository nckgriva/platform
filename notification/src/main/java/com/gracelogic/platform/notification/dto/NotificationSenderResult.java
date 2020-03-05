package com.gracelogic.platform.notification.dto;


public class NotificationSenderResult {
    private boolean success;
    private String errorDescription;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public NotificationSenderResult(boolean success, String errorDescription) {
        this.success = success;
        this.errorDescription = errorDescription;
    }
}
