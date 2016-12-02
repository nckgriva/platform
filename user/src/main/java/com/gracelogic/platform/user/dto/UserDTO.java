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
public class UserDTO extends IdObjectModel implements Serializable {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SURNAME = "surname";

    private String email;
    private String phone;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean approved;
    private Boolean blocked;
    private Map<String, String> fields = new HashMap<>();
    private Set<UUID> roles = new HashSet<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Set<UUID> getRoles() {
        return roles;
    }

    public void setRoles(Set<UUID> roles) {
        this.roles = roles;
    }

    public static UserDTO prepare(User user, UserDTO userDTO) {
        IdObjectModel.prepare(userDTO, user);

        userDTO.setApproved(user.getApproved());
        userDTO.setPhone(user.getPhone());
        userDTO.setEmail(user.getEmail());
        userDTO.setBlocked(user.getBlocked());
        userDTO.setEmailVerified(user.getEmailVerified());
        userDTO.setPhoneVerified(user.getPhoneVerified());

        if (!StringUtils.isEmpty(user.getFields())) {
            userDTO.setFields(JsonUtils.jsonToMap(user.getFields()));
        }

        return userDTO;
    }

    public static UserDTO prepare(User user) {
        UserDTO userDTO = new UserDTO();
        UserDTO.prepare(user, userDTO);
        return userDTO;
    }
}
