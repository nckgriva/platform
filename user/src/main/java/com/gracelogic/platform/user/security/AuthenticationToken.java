package com.gracelogic.platform.user.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String remoteAddress = null;
    private UUID identifierTypeId = null;
    private boolean trust = false;  //for oauth

    public AuthenticationToken(Object principal, Object credentials, String remoteAddress, UUID identifierTypeId, boolean trust) {
        super(principal, credentials);
        this.remoteAddress = remoteAddress;
        this.identifierTypeId = identifierTypeId;
        this.trust = trust;
    }

    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String remoteAddress, UUID identifierTypeId, boolean trust) {
        super(principal, credentials, authorities);
        this.remoteAddress = remoteAddress;
        this.identifierTypeId = identifierTypeId;
        this.trust = trust;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public UUID getIdentifierTypeId() {
        return identifierTypeId;
    }

    public void setIdentifierTypeId(UUID identifierTypeId) {
        this.identifierTypeId = identifierTypeId;
    }

    public boolean isTrust() {
        return trust;
    }

    public void setTrust(boolean trust) {
        this.trust = trust;
    }
}
