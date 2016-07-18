package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.oauth.model.AuthProviderLinkage;
import com.gracelogic.platform.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 10:43
 */
@Service
public class OAuthServiceImpl implements OAuthService {
    @Autowired
    private IdObjectService idObjectService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAuthProviderLinkages(User user) {
        idObjectService.delete(AuthProviderLinkage.class, String.format("el.user.id='%s'", user.getId()));
    }
}
