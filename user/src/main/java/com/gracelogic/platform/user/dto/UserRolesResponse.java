package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.web.dto.PlatformResponse;

import java.util.List;

public class UserRolesResponse extends PlatformResponse {
    private List<RoleDTO> roles = null;

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public UserRolesResponse(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
