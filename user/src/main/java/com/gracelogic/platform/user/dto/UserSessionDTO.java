package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
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

    private String loginType;

    private Boolean valid;

    private String userAgent;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

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

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
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
        dto.setLoginType(model.getLoginType());
        dto.setValid(model.getValid());
        dto.setUserAgent(model.getUserAgent());

        if (model.getUser() != null) {
            dto.setUserId(model.getUser().getId());
        }

        return dto;
    }

    public static void enrich(UserSessionDTO dto, UserSession model) {
        if (model.getUser() != null) {
            dto.setUserName(model.getUser().getFields());
        }
    }
}
