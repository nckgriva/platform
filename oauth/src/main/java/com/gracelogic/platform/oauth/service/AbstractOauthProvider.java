package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.oauth.Path;
import com.gracelogic.platform.oauth.dto.AuthDTO;
import com.gracelogic.platform.oauth.model.AuthProvider;
import com.gracelogic.platform.oauth.model.AuthProviderLinkage;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.IllegalParameterException;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.UserLifecycleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 21.10.2015
 * Time: 17:29
 */
public abstract class AbstractOauthProvider implements OAuthServiceProvider {
    private static Logger logger = Logger.getLogger(AbstractOauthProvider.class);

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private UserLifecycleService registrationService;

    @Autowired
    private PropertyService propertyService;


    protected User processAuth(Integer authProviderId, String code, AuthDTO authDTO) {
        List<AuthProviderLinkage> linkages = idObjectService.getList(AuthProviderLinkage.class, String.format("el.externalUserId='%s' and el.authProvider.id=%d", authDTO.getUserId(), authProviderId), null, null, null, 1);
        if (!linkages.isEmpty() && linkages.size() == 1) {
            AuthProviderLinkage authProviderLinkage = linkages.iterator().next();
            //Existing user
            return authProviderLinkage.getUser();
        }
        else {
            User user = null;

            //Register new user
            AuthorizedUser authorizedUser = new AuthorizedUser();
            authorizedUser.setEmail(authDTO.getEmail());
            authorizedUser.setPhone(!StringUtils.isEmpty(authDTO.getPhone()) ? authDTO.getPhone() : null);
//            authorizedUser.setNickname(!StringUtils.isEmpty(authDTO.getNickname()) ? authDTO.getNickname() : authDTO.getUserId());
            authorizedUser.setName(!StringUtils.isEmpty(authDTO.getFirstName()) ? authDTO.getFirstName() : null);
            authorizedUser.setSurname(!StringUtils.isEmpty(authDTO.getLastName()) ? authDTO.getLastName() : null);
            authorizedUser.setPatronymic(null);

            logger.info("Oauth registration: " + authorizedUser.toString());

            try {
                user = registrationService.register(authorizedUser, true);
            }
            catch (IllegalParameterException e) {
                logger.error("Failed to register user via oauth", e);
            }

            if (user != null) {
                AuthProviderLinkage authProviderLinkage = new AuthProviderLinkage();
                authProviderLinkage.setAuthProvider(idObjectService.getObjectById(AuthProvider.class, authProviderId));
                authProviderLinkage.setUser(user);
                authProviderLinkage.setExternalUserId(authDTO.getUserId());
                authProviderLinkage.setCode(code);
                idObjectService.save(authProviderLinkage);
            }

            return user;
        }
    }

    public String getRedirectUrl(String providerName) {
        return String.format("%s/%s/%s", propertyService.getPropertyValue("web:base_url"), Path.OAUTH, providerName);
    }
}
