package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.web.dto.PlatformRequest;

import java.util.UUID;

public class IdentifierRequestDTO extends PlatformRequest {
    protected UUID identifierTypeId;
    protected String identifierValue;

    public UUID getIdentifierTypeId() {
        return identifierTypeId;
    }

    public void setIdentifierTypeId(UUID identifierTypeId) {
        this.identifierTypeId = identifierTypeId;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }
}
