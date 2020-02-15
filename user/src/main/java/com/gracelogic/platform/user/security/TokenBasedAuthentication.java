package com.gracelogic.platform.user.security;


import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class TokenBasedAuthentication implements Authentication {
    private UUID token;
    private boolean isAuthenticated;
    private AuthorizedUser authorizedUser;
    private Collection<? extends GrantedAuthority> grantedAuthorities;


    public TokenBasedAuthentication(UUID token) {
        this.token = token;
    }

    public void setUserDetails(AuthorizedUser authorizedUser) {
        this.authorizedUser = authorizedUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return authorizedUser;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated  = isAuthenticated;
    }

    @Override
    public String getName() {
        return token.toString();
    }

    public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }
}
