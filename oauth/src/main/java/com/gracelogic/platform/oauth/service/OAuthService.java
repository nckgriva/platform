package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.user.model.User;

public interface OAuthService {
    void deleteAuthProviderLinkages(User user);
}
