package com.gracelogic.platform.user.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: Igor Parkhomenko
 * Date: 10.08.2016
 * Time: 9:28
 */
public class RoleDTO {
    private String code;
    private String name;
    private Set<GrantDTO> grants = new HashSet<GrantDTO>();

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

    public Set<GrantDTO> getGrants() {
        return grants;
    }

    public void setGrants(Set<GrantDTO> grants) {
        this.grants = grants;
    }
}
