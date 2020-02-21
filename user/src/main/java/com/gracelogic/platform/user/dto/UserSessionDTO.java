package com.gracelogic.platform.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.db.dto.JsonDateDeserializer;
import com.gracelogic.platform.db.dto.JsonDateSerializer;
import com.gracelogic.platform.user.model.UserSession;

import java.util.Date;
import java.util.UUID;

public class UserSessionDTO extends IdObjectDTO {

    private String sessionId;

    private Date sessionCreatedDt;

    private Long maxInactiveInterval;

    private Date lastAccessDt;

    private UUID userId;

    private String userName;

    private String authIp;

    private UUID identifierId;

    private Boolean valid;

    private String userAgent;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getSessionCreatedDt() {
        return sessionCreatedDt;
    }

    public void setSessionCreatedDt(Date sessionCreatedDt) {
        this.sessionCreatedDt = sessionCreatedDt;
    }

    public Long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(Long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getLastAccessDt() {
        return lastAccessDt;
    }

    public void setLastAccessDt(Date lastAccessDt) {
        this.lastAccessDt = lastAccessDt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthIp() {
        return authIp;
    }

    public void setAuthIp(String authIp) {
        this.authIp = authIp;
    }

    public UUID getIdentifierId() {
        return identifierId;
    }

    public void setIdentifierId(UUID identifierId) {
        this.identifierId = identifierId;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public static UserSessionDTO prepare(UserSession model) {
        UserSessionDTO dto = new UserSessionDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setSessionId(model.getSessionId());
        dto.setSessionCreatedDt(model.getSessionCreatedDt());
        dto.setMaxInactiveInterval(model.getMaxInactiveInterval());
        dto.setLastAccessDt(model.getLastAccessDt());
        dto.setAuthIp(model.getAuthIp());
        if (model.getIdentifier() != null) {
            dto.setIdentifierId(model.getIdentifier().getId());
        }
        dto.setValid(model.getValid());
        dto.setUserAgent(model.getUserAgent());

        if (model.getUser() != null) {
            dto.setUserId(model.getUser().getId());
        }

        return dto;
    }

    public static void enrich(UserSessionDTO dto, UserSession model) {
        if (model.getUser() != null) {
            dto.setUserName(UserDTO.formatUserName(model.getUser()));
        }
    }
}
