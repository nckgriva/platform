package com.gracelogic.platform.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.db.dto.JsonDateDeserializer;
import com.gracelogic.platform.db.dto.JsonDateSerializer;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.db.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.DefaultMustacheFactory;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class UserDTO extends IdObjectDTO implements Serializable {
    private static String USER_NAME_FORMAT = "{{name}} {{surname}}";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SURNAME = "surname";
    public static final String FIELD_ORG = "org";

    private Boolean approved;
    private Boolean blocked;
    private Map<String, String> fields = new HashMap<>();
    private Set<UUID> roles = new HashSet<>();
    private String formattedUserName;
    private String locale;
    private String password;
    private List<IdentifierDTO> identifiers = new LinkedList<>();

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date blockAfterDt;
    private String authScheduleCronExpression;

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
    public List<IdentifierDTO> getIdentifiers() {
        return identifiers;
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

    public void setIdentifiers(List<IdentifierDTO> identifiers) {
        this.identifiers = identifiers;
    }


    public static UserDTO prepare(User user, UserDTO userDTO) {
        IdObjectDTO.prepare(userDTO, user);

        userDTO.setApproved(user.getApproved());
        userDTO.setBlocked(user.getBlocked());
        userDTO.setFormattedUserName(formatUserName(user));
        userDTO.setLocale(user.getLocale());
        userDTO.setBlockAfterDt(user.getBlockAfterDt());
        userDTO.setAuthScheduleCronExpression(user.getAuthScheduleCronExpression());

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
