package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.dto.UserRegistrationDTO;
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
    public User register(UserRegistrationDTO userRegistrationDTO, boolean trust) throws IllegalParameterException {
        return super.register(userRegistrationDTO, trust);
    }

    @Override
    public void delete(User user) {
        getOAuthService().deleteAuthProviderLinkages(user);
        super.delete(user);
    }
}
