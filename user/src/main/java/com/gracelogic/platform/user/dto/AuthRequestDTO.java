package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.web.dto.PlatformRequest;

import java.util.UUID;

public class AuthRequestDTO extends PlatformRequest {
    private String identifierValue;
    private String password;
    private UUID identifierTypeId;

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthRequestDTO(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public UUID getIdentifierTypeId() {
        return identifierTypeId;
    }

    public void setIdentifierTypeId(UUID identifierTypeId) {
        this.identifierTypeId = identifierTypeId;
    }

    public AuthRequestDTO() {
    }

    @Override
    public String toString() {
        return "AuthRequestDTO{" +
                "identifierValue='" + identifierValue + '\'' +
                ", password='" + password + '\'' +
                ", identifierTypeId=" + identifierTypeId +
                '}';
    }
}
