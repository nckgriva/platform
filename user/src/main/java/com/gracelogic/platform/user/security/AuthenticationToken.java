package com.gracelogic.platform.user.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Author: Igor Parkhomenko
 * Date: 13.06.14
 * Time: 22:01
 */
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String remoteAddress = null;
    private String loginType = null;
    private String role;

    public AuthenticationToken(Object principal, Object credentials, String remoteAddress, String loginType, String role) {
        super(principal, credentials);
        this.remoteAddress = remoteAddress;
        this.loginType = loginType;
        this.role = role;
    }

    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String remoteAddress, String loginType, String role) {
        super(principal, credentials, authorities);
        this.remoteAddress = remoteAddress;
        this.loginType = loginType;
        this.role = role;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
