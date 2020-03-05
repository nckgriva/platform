package com.gracelogic.platform.notification.method.push;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FcmResult {
    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("registration_id")
    private String registrationId;

    @JsonProperty("error")
    private String error;

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