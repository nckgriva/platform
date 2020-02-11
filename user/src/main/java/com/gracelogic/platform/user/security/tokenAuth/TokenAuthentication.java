package com.gracelogic.platform.user.security.tokenAuth;


import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class TokenAuthentication implements Authentication {

    private String token;
    private boolean isAuthenticated;
    private AuthorizedUser authorizedUser;
    private Collection<? extends GrantedAuthority> grantedAuthorities;


    public TokenAuthentication(String token) {
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
        return token;
    }

    public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }
}
