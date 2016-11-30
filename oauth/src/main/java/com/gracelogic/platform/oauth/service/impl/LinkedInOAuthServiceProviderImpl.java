package com.gracelogic.platform.oauth.service.impl;

import com.gracelogic.platform.oauth.DataConstants;
import com.gracelogic.platform.oauth.dto.OAuthDTO;
import com.gracelogic.platform.oauth.service.AbstractOauthProvider;
import com.gracelogic.platform.oauth.service.OAuthServiceProvider;
import com.gracelogic.platform.oauth.service.OAuthUtils;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;

/**
 * Author: Igor Parkhomenko
 * Date: 21.10.2015
 * Time: 13:15
 */
@Service("linkedin")
public class LinkedInOAuthServiceProviderImpl extends AbstractOauthProvider implements OAuthServiceProvider {
    private static Logger logger = Logger.getLogger(LinkedInOAuthServiceProviderImpl.class);

    //private static final String CLIENT_ID = "78qkahnfqw17u8";
    //private static final String CLIENT_SECRET = "tIf3q6Vl6XXd460V";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String API_ENDPOINT = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address)?oauth2_access_token=%s&format=json";

    @Autowired
    private PropertyService propertyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User accessToken(String code, String redirectUri) {
        String CLIENT_ID = propertyService.getPropertyValue("oauth:linkedin_client_id");
        String CLIENT_SECRET = propertyService.getPropertyValue("oauth:linkedin_client_secret");

        String sRedirectUri = redirectUri;
        if (StringUtils.isEmpty(redirectUri)) {
            sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.LINKEDIN.name());

            try {
                sRedirectUri = URLEncoder.encode(sRedirectUri, "UTF-8");
            }
            catch (Exception ignored) {}
        }
        logger.info("REDIRECT_URL: " + redirectUri);

        Map response = OAuthUtils.postTextBodyReturnJson(ACCESS_TOKEN_ENDPOINT, String.format("grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s", code, CLIENT_ID, CLIENT_SECRET, sRedirectUri));
        if (response == null) {
            return null;
        }
        logger.info("1");

        OAuthDTO OAuthDTO = new OAuthDTO();
        OAuthDTO.setAccessToken(response.get("access_token") != null ? (String) response.get("access_token") : null);
        logger.info("2");
        response = OAuthUtils.getQueryReturnJson(String.format(API_ENDPOINT, OAuthDTO.getAccessToken()));
        OAuthDTO.setUserId(response.get("id") != null ? (String) response.get("id") : null);
        logger.info("3");
        OAuthDTO.setFirstName(response.get("firstName") != null ? (String) response.get("firstName") : null);
        OAuthDTO.setLastName(response.get("lastName") != null ? (String) response.get("lastName") : null);
        OAuthDTO.setEmail(response.get("emailAddress") != null ? (String) response.get("emailAddress") : null);

        return processAuth(DataConstants.OAuthProviders.LINKEDIN.getValue(), code, OAuthDTO);
    }

    @Override
    public String buildAuthRedirect() {
        String CLIENT_ID = propertyService.getPropertyValue("oauth:linkedin_client_id");
        String sRedirectUri = buildRedirectUri(null);

        return String.format("https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=%s&redirect_uri=%s&state=987654321&scope=%s",CLIENT_ID, sRedirectUri, "r_basicprofile%20r_emailaddress");
    }

    @Override
    public String buildRedirectUri(String additionalParameters) {
        String sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.LINKEDIN.name());
        if (!StringUtils.isEmpty(additionalParameters)) {
            sRedirectUri = sRedirectUri + additionalParameters;
        }
        try {
            sRedirectUri = URLEncoder.encode(sRedirectUri, "UTF-8");
        }
        catch (Exception ignored) {}

        return sRedirectUri;
    }
}
