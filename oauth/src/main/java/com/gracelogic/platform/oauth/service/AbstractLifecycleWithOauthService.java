package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.IllegalParameterException;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.AbstractLifecycleService;
import com.gracelogic.platform.user.service.UserLifecycleService;

/**
 * Author: Igor Parkhomenko
 * Date: 17.07.2016
 * Time: 22:47
 */
public abstract class AbstractLifecycleWithOauthService extends AbstractLifecycleService implements UserLifecycleService {
    protected abstract OAuthService getOAuthService();

    @Override
    public User register(AuthorizedUser user, boolean trust) throws IllegalParameterException {
        return super.register(user, trust);
    }

    @Override
    public void delete(User user) {
        getOAuthService().deleteAuthProviderLinkages(user);
        super.delete(user);
    }
}
