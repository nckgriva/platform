package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.oauth.Path;
import com.gracelogic.platform.oauth.dto.AuthDTO;
import com.gracelogic.platform.oauth.model.AuthProvider;
import com.gracelogic.platform.oauth.model.AuthProviderLinkage;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.UserRegistrationDTO;
import com.gracelogic.platform.user.exception.IllegalParameterException;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.UserLifecycleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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


    protected User processAuth(UUID authProviderId, String code, AuthDTO authDTO) {
        Map<String, Object> params = new HashMap<>();
        params.put("externalUserId", authDTO.getUserId());
        params.put("authProviderId", authProviderId);
        List<AuthProviderLinkage> linkages = idObjectService.getList(AuthProviderLinkage.class, null, "el.externalUserId=:externalUserId and el.authProvider.id=:authProviderId", params, null, null, null, 1);
        if (!linkages.isEmpty() && linkages.size() == 1) {
            AuthProviderLinkage authProviderLinkage = linkages.iterator().next();
            //Existing user
            return authProviderLinkage.getUser();
        }
        else {
            User user = null;

            //Register new user
            UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
            userRegistrationDTO.setEmail(authDTO.getEmail());
            userRegistrationDTO.setPhone(!StringUtils.isEmpty(authDTO.getPhone()) ? authDTO.getPhone() : null);

            userRegistrationDTO.getFields().put("name", !StringUtils.isEmpty(authDTO.getFirstName()) ? authDTO.getFirstName() : null);
            userRegistrationDTO.getFields().put("surname", !StringUtils.isEmpty(authDTO.getLastName()) ? authDTO.getLastName() : null);
            userRegistrationDTO.getFields().put("patronymic", null);

            logger.info("Oauth registration: " + userRegistrationDTO.toString());

            try {
                user = registrationService.register(userRegistrationDTO, true);
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
