package com.gracelogic.platform.user.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.db.model.StringJsonUserType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "USER")
@TypeDefs({@TypeDef(name= "stringJsonObject", typeClass = StringJsonUserType.class)})
public class User extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @Access(AccessType.PROPERTY)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "PASSWORD", nullable = true)
    private String password;

    @Column(name = "SALT", nullable = true)
    private String salt;

    @Column(name = "EMAIL", nullable = true, unique = true)
    private String email;

    @Column(name = "PHONE", nullable = true, unique = true)
    private String phone;

    @Column(name = "IS_PHONE_VERIFIED", nullable = false)
    private Boolean phoneVerified;

    @Column(name = "IS_EMAIL_VERIFIED", nullable = false)
    private Boolean emailVerified;

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

    @ManyToOne
    @JoinColumn(name = "BLOCKED_BY_USER_ID", nullable = true)
    private User blockedByUser;

    @Column(name = "LAST_VISIT_DT", nullable = true)
    private Date lastVisitDt;

    @Column(name = "LAST_VISIT_IP", nullable = true)
    private String lastVisitIP;

    @Type(type = "stringJsonObject")
    @Column(columnDefinition = "json", nullable = true)
    private String fields;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAllowedAddresses() {
        return allowedAddresses;
    }

    public void setAllowedAddresses(String allowedAddresses) {
        this.allowedAddresses = allowedAddresses;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
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
}
