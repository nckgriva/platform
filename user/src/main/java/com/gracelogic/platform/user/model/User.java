package com.gracelogic.platform.user.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "USER")
public class User extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @Access(AccessType.PROPERTY)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<UserRole> userRoles = new HashSet<UserRole>();

    @Column(name = "IS_APPROVED", nullable = false)
    private Boolean approved;

    @Column(name = "ALLOWED_ADDRESSES", nullable = true)
    private String allowedAddresses;

    @Column(name = "IS_BLOCKED", nullable = false)
    private Boolean blocked;

    @Column(name = "BLOCKED_DT", nullable = true)
    private Date blockedDt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "BLOCKED_BY_USER_ID", nullable = true)
    private User blockedByUser;

    @Column(name = "LAST_VISIT_DT", nullable = true)
    private Date lastVisitDt;

    @Column(name = "LAST_VISIT_IP", nullable = true)
    private String lastVisitIP;

    @Type(JsonType.class)
    @Column(columnDefinition = "json", nullable = true, name = "FIELDS")
    private String fields;

    @Column(name = "LOCALE", nullable = true)
    private String locale;

    @Column(name = "BLOCK_AFTER_DT", nullable = true)
    private Date blockAfterDt;

    @Column(name = "AUTH_SCHEDULE_CRON_EXPRESSION", nullable = true)
    private String authScheduleCronExpression;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public String getAllowedAddresses() {
        return allowedAddresses;
    }

    public void setAllowedAddresses(String allowedAddresses) {
        this.allowedAddresses = allowedAddresses;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Date getBlockedDt() {
        return blockedDt;
    }

    public void setBlockedDt(Date blockedDt) {
        this.blockedDt = blockedDt;
    }

    public User getBlockedByUser() {
        return blockedByUser;
    }

    public void setBlockedByUser(User blockedByUser) {
        this.blockedByUser = blockedByUser;
    }

    public Date getLastVisitDt() {
        return lastVisitDt;
    }

    public void setLastVisitDt(Date lastVisitDt) {
        this.lastVisitDt = lastVisitDt;
    }

    public String getLastVisitIP() {
        return lastVisitIP;
    }

    public void setLastVisitIP(String lastVisitIP) {
        this.lastVisitIP = lastVisitIP;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getBlockAfterDt() {
        return blockAfterDt;
    }

    public void setBlockAfterDt(Date blockAfterDt) {
        this.blockAfterDt = blockAfterDt;
    }

    public String getAuthScheduleCronExpression() {
        return authScheduleCronExpression;
    }

    public void setAuthScheduleCronExpression(String authScheduleCronExpression) {
        this.authScheduleCronExpression = authScheduleCronExpression;
    }
}
