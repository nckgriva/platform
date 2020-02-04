package com.gracelogic.platform.user.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "USER_SESSION")
public class UserSession extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "com.gracelogic.platform.db.type.UUIDCustomType")
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "SESSION_ID", nullable = true)
    private String sessionId;

    @Column(name = "SESSION_CREATED_DT", nullable = true)
    private Date sessionCreatedDt;

    @Column(name = "MAX_INACTIVE_INTERVAL", nullable = true)
    private Long maxInactiveInterval;

    @Column(name = "LAST_ACCESS_DT", nullable = true)
    private Date lastAccessDt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;

    @Column(name = "AUTH_IP", nullable = true)
    private String authIp;

    @Column(name = "LOGIN_TYPE", nullable = true)
    private String loginType;

    @Column(name = "IS_VALID", nullable = true)
    private Boolean valid;

    @Column(name = "USER_AGENT", nullable = true, length = 1024)
    private String userAgent;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
}
