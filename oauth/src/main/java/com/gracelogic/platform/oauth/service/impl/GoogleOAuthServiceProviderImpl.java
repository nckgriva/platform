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
@Service("google")
public class GoogleOAuthServiceProviderImpl extends AbstractOauthProvider implements OAuthServiceProvider {
    private static Logger logger = Logger.getLogger(GoogleOAuthServiceProviderImpl.class);

    //private static final String CLIENT_ID = "455561325092-6t8utfr5bvbqgmgatc8bkaop96qjkko2.apps.googleusercontent.com";
    //private static final String CLIENT_SECRET = "C8z3Ox69EC_W8FpFgpyyuvDE";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://accounts.google.com/o/oauth2/token";
    private static final String API_ENDPOINT = "https://www.googleapis.com/plus/v1/people/me?access_token=%s&fields=id,name,emails";

    @Autowired
    private PropertyService propertyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User accessToken(String code, String redirectUri) {
        String CLIENT_ID = propertyService.getPropertyValue("oauth:google_client_id");
        String CLIENT_SECRET = propertyService.getPropertyValue("oauth:google_client_secret");

        String sRedirectUri = redirectUri;
        if (StringUtils.isEmpty(redirectUri)) {
            sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.GOOGLE.name());

            try {
                sRedirectUri = URLEncoder.encode(sRedirectUri, "UTF-8");
            }
            catch (Exception ignored) {}
        }

        Map response = OAuthUtils.postTextBodyReturnJson(ACCESS_TOKEN_ENDPOINT, String.format("code=%s&client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code", code, CLIENT_ID, CLIENT_SECRET, sRedirectUri));
        if (response == null) {
            return null;
        }

        OAuthDTO OAuthDTO = new OAuthDTO();
        OAuthDTO.setAccessToken(response.get("access_token") != null ? (String) response.get("access_token") : null);

        response = OAuthUtils.getQueryReturnJson(String.format(API_ENDPOINT, OAuthDTO.getAccessToken()));
        OAuthDTO.setUserId(response.get("id") != null ? (String) response.get("id") : null);

        Map name = (Map) response.get("name");
        if (name != null) {
            OAuthDTO.setFirstName(name.get("givenName") != null ? (String) name.get("givenName") : null);
            OAuthDTO.setLastName(name.get("familyName") != null ? (String) name.get("familyName") : null);
        }

        Collection<Map> emails = (Collection<Map>) response.get("emails");
        if (emails != null && !emails.isEmpty()) {
            Map email = emails.iterator().next();
            OAuthDTO.setEmail(email.get("value") != null ? (String) email.get("value") : null);
        }

        return processAuth(DataConstants.OAuthProviders.GOOGLE.getValue(), code, OAuthDTO);
    }

    @Override
    public String buildAuthRedirect() {
        String CLIENT_ID = propertyService.getPropertyValue("oauth:google_client_id");
        String sRedirectUri = buildRedirectUri(null);

        return String.format("https://accounts.google.com/o/oauth2/auth?redirect_uri=%s&response_type=code&client_id=%s&scope=email", sRedirectUri, CLIENT_ID);
    }

    @Override
    public String buildRedirectUri(String additionalParameters) {
        String sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.GOOGLE.name());
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
