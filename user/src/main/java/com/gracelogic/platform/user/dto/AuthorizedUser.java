package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectModel;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 11.08.12
 * Time: 21:24
 */
public class AuthorizedUser extends IdObjectModel implements Serializable {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SURNAME = "surname";

    private String email;
    private String phone;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean approved;

    private Boolean blocked;

    //Transient fields
    private String password;
    private UUID userSessionId;

    private Set<String> grants = new HashSet<>();
    private Set<UUID> roles = new HashSet<>();
    private Map<String, String> fields = new HashMap<>();


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public UUID getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(UUID userSessionId) {
        this.userSessionId = userSessionId;
    }

    public Set<String> getGrants() {
        return grants;
    }

    public void setGrants(Set<String> grants) {
        this.grants = grants;
    }

    public Set<UUID> getRoles() {
        return roles;
    }

    public void setRoles(Set<UUID> roles) {
        this.roles = roles;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public static AuthorizedUser prepare(User user, AuthorizedUser authorizedUser) {
        IdObjectModel.prepare(authorizedUser, user);

        authorizedUser.setApproved(user.getApproved());
        authorizedUser.setPhone(user.getPhone());
        authorizedUser.setEmail(user.getEmail());
        authorizedUser.setBlocked(user.getBlocked());
        authorizedUser.setEmailVerified(user.getEmailVerified());
        authorizedUser.setPhoneVerified(user.getPhoneVerified());

        if (!StringUtils.isEmpty(user.getFields())) {
            authorizedUser.setFields(JsonUtils.jsonToMap(user.getFields()));
        }

        return authorizedUser;
    }

    public static AuthorizedUser prepare(User user) {
        AuthorizedUser authorizedUser = new AuthorizedUser();
        AuthorizedUser.prepare(user, authorizedUser);
        return authorizedUser;
    }
}
