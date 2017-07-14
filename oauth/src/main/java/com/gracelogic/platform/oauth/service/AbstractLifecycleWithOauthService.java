package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.AbstractLifecycleService;
import com.gracelogic.platform.user.service.UserLifecycleService;

public abstract class AbstractLifecycleWithOauthService extends AbstractLifecycleService implements UserLifecycleService {
    protected abstract OAuthService getOAuthService();

    @Override
    public void delete(User user) {
        getOAuthService().deleteAuthProviderLinkages(user);
        super.delete(user);
    }
}
