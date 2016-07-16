package com.gracelogic.platform.oauth.service;


import com.gracelogic.platform.user.model.User;

/**
 * Author: Igor Parkhomenko
 * Date: 21.10.2015
 * Time: 13:06
 */
public interface OAuthServiceProvider {
    User accessToken(String code, String redirectUri);

    String buildAuthRedirect();

    String buildRedirectUri(String additionalParameters);
}
