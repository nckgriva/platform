package com.gracelogic.platform.oauth.service.impl;

import com.gracelogic.platform.oauth.DataConstants;
import com.gracelogic.platform.oauth.dto.OAuthDTO;
import com.gracelogic.platform.oauth.service.AbstractOauthProvider;
import com.gracelogic.platform.oauth.service.OAuthServiceProvider;
import com.gracelogic.platform.oauth.service.OAuthUtils;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.Map;

@Service("instagram")
public class InstagramOAuthServiceProviderImpl extends AbstractOauthProvider implements OAuthServiceProvider {
    //private static final String CLIENT_ID = "6e538b20833949b6ab179e8fbcb48eef";
    //private static final String CLIENT_SECRET = "3d892ed595ad4b59aaddd17848c95e9a";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://api.instagram.com/oauth/access_token";

    @Autowired
    private PropertyService propertyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User processAuthorization(String code, String redirectUri) {
        String CLIENT_ID = propertyService.getPropertyValue("oauth:instagram_client_id");
        String CLIENT_SECRET = propertyService.getPropertyValue("oauth:instagram_client_secret");

        String sRedirectUri = redirectUri;
        if (StringUtils.isEmpty(redirectUri)) {
            sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.INSTAGRAM.name());

            try {
                sRedirectUri = URLEncoder.encode(sRedirectUri, "UTF-8");
            }
            catch (Exception ignored) {}
        }

        Map response = OAuthUtils.postTextBodyReturnJson(ACCESS_TOKEN_ENDPOINT, String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s", CLIENT_ID, CLIENT_SECRET, code, sRedirectUri));

        if (response == null) {
            return null;
        }

        OAuthDTO OAuthDTO = new OAuthDTO();
        OAuthDTO.setAccessToken(response.get("access_token") != null ? (String) response.get("access_token") : null);

        response = (Map) response.get("user");

        OAuthDTO.setUserId(response.get("id") != null ? (String) response.get("id") : null);
        String fullName = response.get("full_name") != null ? (String) response.get("full_name") : null;
        String name = null;
        String surname = null;
        if (!StringUtils.isEmpty(fullName)) {
            int spacePos = fullName.indexOf(" ");
            if (spacePos != -1) {
                name = fullName.substring(0, spacePos);
                if (spacePos < fullName.length()) {
                    surname = fullName.substring(spacePos + 1);
                }
            }
            else {
                name = fullName;
            }
        }

        OAuthDTO.setFirstName(name);
        OAuthDTO.setLastName(surname);

        return processAuthorization(DataConstants.OAuthProviders.INSTAGRAM.getValue(), code, OAuthDTO);
    }

    @Override
    public String buildAuthRedirect() {
        String CLIENT_ID = propertyService.getPropertyValue("oauth:instagram_client_id");
        String sRedirectUri = buildRedirectUri(null);

        return String.format("https://api.instagram.com/oauth/authorize/?client_id=%s&response_type=code&redirect_uri=%s", CLIENT_ID, sRedirectUri);
    }

    @Override
    public String buildRedirectUri(String additionalParameters) {
        String sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.INSTAGRAM.name());
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
