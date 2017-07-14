package com.gracelogic.platform.user.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String remoteAddress = null;
    private String loginType = null;
    private boolean trust = false;  //for oauth

    public AuthenticationToken(Object principal, Object credentials, String remoteAddress, String loginType, boolean trust) {
        super(principal, credentials);
        this.remoteAddress = remoteAddress;
        this.loginType = loginType;
        this.trust = trust;
    }

    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String remoteAddress, String loginType, boolean trust) {
        super(principal, credentials, authorities);
        this.remoteAddress = remoteAddress;
        this.loginType = loginType;
        this.trust = trust;
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

    public boolean isTrust() {
        return trust;
    }

    public void setTrust(boolean trust) {
        this.trust = trust;
    }
}
