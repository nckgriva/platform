package com.gracelogic.platform.user.dto;


import com.gracelogic.platform.db.dto.IdObjectModel;
import com.gracelogic.platform.user.model.User;

import java.io.Serializable;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 11.08.12
 * Time: 21:24
 */
public class AuthorizedUser extends IdObjectModel implements Serializable {
    private String email;
    private Boolean approved;
    private String phone;

    private Boolean blocked;
    private Boolean emailVerified;
    private Boolean phoneVerified;

    //Transient fields
    private String password;
    private String name;
    private String surname;
    private String patronymic;
    private UUID userSessionId;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
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

    public static AuthorizedUser prepare(User user, AuthorizedUser authorizedUser) {
        IdObjectModel.prepare(authorizedUser, user);

        authorizedUser.setApproved(user.getApproved());
        authorizedUser.setPhone(user.getPhone());
        authorizedUser.setEmail(user.getEmail());
        authorizedUser.setBlocked(user.getBlocked());
        authorizedUser.setEmailVerified(user.getEmailVerified());
        authorizedUser.setPhoneVerified(user.getPhoneVerified());

        return authorizedUser;
    }

    public static AuthorizedUser prepare(User user) {
        AuthorizedUser authorizedUser = new AuthorizedUser();
        AuthorizedUser.prepare(user, authorizedUser);
        return authorizedUser;
    }
}
