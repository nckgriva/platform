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
import java.util.ArrayList;
import java.util.Map;

@Service("vk")
public class VkOAuthServiceProviderImpl extends AbstractOauthProvider implements OAuthServiceProvider {
    //private static final String CLIENT_ID = "5116200";
    //private static final String CLIENT_SECRET = "9EJ0oeLfqF0mpFLMP8gg";
    private static final String ACCESS_TOKEN_ENDPOINT = "https://oauth.vk.com/access_token";

    private static final String API_ENDPOINT = "https://api.vk.com/method/users.get";

    @Autowired
    private PropertyService propertyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User accessToken(String code, String redirectUri) {
        String CLIENT_ID = propertyService.getPropertyValue("oauth:vk_client_id");
        String CLIENT_SECRET = propertyService.getPropertyValue("oauth:vk_client_secret");

        String sRedirectUri = redirectUri;
        if (StringUtils.isEmpty(redirectUri)) {
            sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.VK.name());

            try {
                sRedirectUri = URLEncoder.encode(sRedirectUri, "UTF-8");
            }
            catch (Exception ignored) {}
        }


        Map response = OAuthUtils.getQueryReturnJson(String.format("%s?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s", ACCESS_TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET, code, sRedirectUri));

        if (response == null) {
            return null;
        }

        OAuthDTO OAuthDTO = new OAuthDTO();
        OAuthDTO.setAccessToken(response.get("access_token") != null ? (String) response.get("access_token") : null);
        OAuthDTO.setUserId(response.get("user_id") != null ? String.valueOf(response.get("user_id")) : null);
        OAuthDTO.setEmail(response.get("email") != null ? (String) response.get("email") : null);

        response = OAuthUtils.getQueryReturnJson(String.format("%s?user_ids=%s&v=5.27&fields=photo_100,city,verified,contacts&access_token=%s", API_ENDPOINT, OAuthDTO.getUserId(), OAuthDTO.getAccessToken()));

        response = (Map) ((ArrayList) response.get("response")).iterator().next();

        OAuthDTO.setFirstName(response.get("first_name") != null ? (String) response.get("first_name") : null);
        OAuthDTO.setLastName(response.get("last_name") != null ? (String) response.get("last_name") : null);
        OAuthDTO.setPhone(response.get("mobile_phone") != null ? (String) response.get("mobile_phone") : null);

        return processAuth(DataConstants.OAuthProviders.VK.getValue(), code, OAuthDTO);
    }

    @Override
    public String buildAuthRedirect() {
        String sRedirectUri = buildRedirectUri(null);
        String CLIENT_ID = propertyService.getPropertyValue("oauth:vk_client_id");

        return String.format("https://oauth.vk.com/authorize?client_id=%s&response_type=code&scope=email,phone&display=popup&redirect_uri=%s", CLIENT_ID, sRedirectUri);
    }

    @Override
    public String buildRedirectUri(String additionalParameters) {
        String sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.VK.name());
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
