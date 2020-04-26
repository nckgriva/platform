package com.gracelogic.platform.oauth.dto;

import java.util.UUID;

public class TokenByCodeRequestDTO {
    private UUID authProviderId;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UUID getAuthProviderId() {
        return authProviderId;
    }

    public void setAuthProviderId(UUID authProviderId) {
        this.authProviderId = authProviderId;
    }
}
