package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.oauth.dto.AuthProviderDTO;
import com.gracelogic.platform.user.model.User;

import java.util.List;

public interface OAuthService {
    void deleteAuthProviderLinkages(User user);

    List<AuthProviderDTO> getAuthProviders();
}