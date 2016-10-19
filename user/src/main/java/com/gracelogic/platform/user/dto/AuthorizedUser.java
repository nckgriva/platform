package com.gracelogic.platform.user.dto;


import com.gracelogic.platform.user.model.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 11.08.12
 * Time: 21:24
 */
public class AuthorizedUser extends UserDTO implements Serializable {
    private Set<String> grants = new HashSet<>();
    private Set<UUID> roles = new HashSet<>();

    private UUID userSessionId;

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

    public UUID getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(UUID userSessionId) {
        this.userSessionId = userSessionId;
    }

    public static AuthorizedUser prepare(User user) {
        AuthorizedUser authorizedUser = new AuthorizedUser();
        UserDTO.prepare(user, authorizedUser);
        return authorizedUser;
    }
}
