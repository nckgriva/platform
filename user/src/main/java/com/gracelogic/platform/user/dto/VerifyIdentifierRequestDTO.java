package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.web.dto.PlatformRequest;

import java.util.UUID;

public class VerifyIdentifierRequestDTO extends PlatformRequest {
    private UUID identifierId;
    private String verificationCode;

    public UUID getIdentifierId() {
        return identifierId;
    }

    public void setIdentifierId(UUID identifierId) {
        this.identifierId = identifierId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
