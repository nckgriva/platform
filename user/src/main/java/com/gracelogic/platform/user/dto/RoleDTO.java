package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.user.model.Role;
import com.gracelogic.platform.user.model.UserRole;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RoleDTO extends IdObjectDTO {
    private String code;
    private String name;
    private String description;
    private Set<UUID> grants = new HashSet<UUID>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UUID> getGrants() {
        return grants;
    }

    public void setGrants(Set<UUID> grants) {
        this.grants = grants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static RoleDTO prepare(Role role) {
        RoleDTO model = new RoleDTO();
        IdObjectDTO.prepare(model, role);
        model.setCode(role.getCode());
        model.setName(role.getName());
        model.setDescription(role.getDescription());

        return model;
    }

    public static RoleDTO prepare(UserRole userRole) {
        return prepare(userRole.getRole());
    }
}
