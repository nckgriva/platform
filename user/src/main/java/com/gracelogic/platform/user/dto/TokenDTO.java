package com.gracelogic.platform.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.db.dto.JsonDateDeserializer;
import com.gracelogic.platform.db.dto.JsonDateSerializer;
import com.gracelogic.platform.user.model.Token;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class TokenDTO extends IdObjectDTO implements Serializable {

    private UUID token;

    private UUID userId;
    private String userName;
    private UUID identifierId;
    private String identifierValue;
    private boolean active;

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date lastRequest;
    private String additionalFields;

    public TokenDTO() { }

    public TokenDTO(UUID token) { this.token = token; }

    public static TokenDTO prepare(Token token, boolean enrich) {
        TokenDTO tokenDTO = new TokenDTO();
        IdObjectDTO.prepare(tokenDTO, token);

        tokenDTO.setActive(token.getActive());
        tokenDTO.setLastRequest(token.getLastRequest());
        tokenDTO.setAdditionalFields(token.getAdditionalFields());

        if (enrich) {
            tokenDTO.setUserId(token.getUser().getId());
            tokenDTO.setUserName(UserDTO.formatUserName(token.getUser()));
            tokenDTO.setIdentifierId(token.getIdentifier().getId());
            tokenDTO.setIdentifierValue(token.getIdentifier().getValue());
        }

        return tokenDTO;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getIdentifierId() {
        return identifierId;
    }

    public void setIdentifierId(UUID identifierId) {
        this.identifierId = identifierId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Date lastRequest) {
        this.lastRequest = lastRequest;
    }

    public String getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(String additionalFields) {
        this.additionalFields = additionalFields;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }
}
