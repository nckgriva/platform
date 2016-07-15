package com.gracelogic.platform.user.dto;


import com.gracelogic.platform.dao.dto.IdObjectModel;
import com.gracelogic.platform.user.model.User;

import java.io.Serializable;

/**
 * Author: Igor Parkhomenko
 * Date: 11.08.12
 * Time: 21:24
 */
public class UserModel extends IdObjectModel implements Serializable {
    private String email;
    private Boolean approved;
    private String phone;

    private Boolean blocked;

    //Transient fields
    private String password;

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

    public static UserModel prepare(User user, UserModel userModel) {
        IdObjectModel.prepare(userModel, user);

        userModel.setApproved(user.getApproved());
        userModel.setPhone(user.getPhone());
        userModel.setEmail(user.getEmail());
        userModel.setBlocked(user.getBlocked());

        return userModel;
    }

    public static UserModel prepare(User user) {
        UserModel userModel = new UserModel();
        UserModel.prepare(user, userModel);
        return userModel;
    }
}
