package com.gracelogic.platform.oauth.model;


import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.user.model.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 25.06.12
 * Time: 18:30
 */
@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "AUTH_PROVIDER_LKG", schema = JPAProperties.DEFAULT_SCHEMA)
public class AuthProviderLinkage extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "CODE", nullable = true, length = 4000)
    private String code;

    @Column(name = "EXTERNAL_USER_ID", nullable = true, length = 4000)
    private String externalUserId;

    @ManyToOne
    @JoinColumn(name = "AUTH_PROVIDER_ID", nullable = false)
    private AuthProvider authProvider;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
