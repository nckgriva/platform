package com.gracelogic.platform.oauth.service;


import com.gracelogic.platform.user.model.User;

public interface OAuthServiceProvider {
    User accessToken(String code, String redirectUri);

    String buildAuthRedirect();

    String buildRedirectUri(String additionalParameters);
}
