package com.gracelogic.platform.user.dto;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class UserDTO extends IdObjectDTO implements Serializable {
    private static String USER_NAME_FORMAT = "{{name}} {{surname}}";
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
    private String formattedUserName;
    private String locale;
    private String password;

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

    public String getFormattedUserName() {
        return formattedUserName;
    }

    public void setFormattedUserName(String formattedUserName) {
        this.formattedUserName = formattedUserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public static UserDTO prepare(User user, UserDTO userDTO) {
        IdObjectDTO.prepare(userDTO, user);

        userDTO.setApproved(user.getApproved());
        userDTO.setPhone(user.getPhone());
        userDTO.setEmail(user.getEmail());
        userDTO.setBlocked(user.getBlocked());
        userDTO.setEmailVerified(user.getEmailVerified());
        userDTO.setPhoneVerified(user.getPhoneVerified());
        userDTO.setFormattedUserName(formatUserName(user));
        userDTO.setLocale(user.getLocale());

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

    public static String formatUserName(User user) {
        Map<String, String> fields = JsonUtils.jsonToMap(user.getFields());
        fields.put("email", user.getEmail());
        fields.put("phone", user.getPhone());

        String result = null;
        try {
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader(USER_NAME_FORMAT), "USER_NAME_FORMAT");
            result = mustache.execute(new StringWriter(), fields).toString();
        }
        catch (Exception ignored) {}
        return result;
    }

    public static void setUserNameFormat(String format) {
        USER_NAME_FORMAT = format;
    }

}
