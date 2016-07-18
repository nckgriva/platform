package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.user.model.User;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 10:42
 */
public interface OAuthService {
    void deleteAuthProviderLinkages(User user);
}
