package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.web.dto.PlatformRequest;

import java.util.UUID;

public class SendVerificationCodeForPasswordChangingRequestDTO extends PlatformRequest {
    private String identifierValue;
    private UUID identifierTypeId;

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public UUID getIdentifierTypeId() {
        return identifierTypeId;
    }

    public void setIdentifierTypeId(UUID identifierTypeId) {
        this.identifierTypeId = identifierTypeId;
    }
}
