package com.gracelogic.platform.notification.firebase.protocol;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FcmResult {

    private String messageId;

    private String registrationId;

    private String error;

    public FcmResult() {
    }

    public FcmResult(String messageId, String registrationId, String error) {
        this.messageId = messageId;
        this.registrationId = registrationId;
        this.error = error;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
