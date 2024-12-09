package com.gracelogic.platform.oauth.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.oauth.DataConstants;
import com.gracelogic.platform.oauth.dto.OAuthDTO;
import com.gracelogic.platform.oauth.model.AuthProvider;
import com.gracelogic.platform.oauth.service.AbstractOauthProvider;
import com.gracelogic.platform.oauth.service.OAuthServiceProvider;
import com.gracelogic.platform.user.exception.CustomLocalizedException;
import com.gracelogic.platform.user.exception.InvalidIdentifierException;
import com.gracelogic.platform.user.exception.InvalidPassphraseException;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

@Service("eais")
public class EaisOAuthServiceProviderImpl extends AbstractOauthProvider implements OAuthServiceProvider {
    private String ACCESS_TOKEN_ENDPOINT = "";
    private String AC_URL = "";
    private String INFO_ENDPOINT = "";
    private String CLIENT_ID = null;
    private String CLIENT_SECRET = null;

    @Autowired
    private IdObjectService idObjectService;

    private static Log logger = LogFactory.getLog(EaisOAuthServiceProviderImpl.class);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User processAuthorization(String code, String accessToken, String redirectUri) throws InvalidIdentifierException, InvalidPassphraseException, CustomLocalizedException {
        String sRedirectUri = redirectUri;
        if (StringUtils.isEmpty(redirectUri)) {
            sRedirectUri = getRedirectUrl(DataConstants.OAuthProviders.EAIS.name());

            try {
                sRedirectUri = URLEncoder.encode(sRedirectUri, "UTF-8");
            }
            catch (Exception ignored) {}
        }

        String credentials = new String(CLIENT_ID + ":" + CLIENT_SECRET);
        logger.info("Credentials: " + credentials);
        Map response = postQueryWithAuthenticationReturnJson(String.format("%s?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s", ACCESS_TOKEN_ENDPOINT, CLIENT_ID, CLIENT_SECRET, code, sRedirectUri), "Basic " + new String(Base64.encodeBase64(credentials.getBytes())));

        if (response == null) {
            return null;
        }

        OAuthDTO OAuthDTO = new OAuthDTO();
        OAuthDTO.setAccessToken(response.get("access_token") != null ? (String) response.get("access_token") : null);
        OAuthDTO.setUserId(response.get("urn:esia:sbj_id") != null ? String.valueOf(response.get("urn:esia:sbj_id")) : null);

        response = getQueryWithAuthenticationReturnJson(String.format("%s/%s", INFO_ENDPOINT, OAuthDTO.getUserId()), "Bearer " + OAuthDTO.getAccessToken());

        OAuthDTO.setLastName(response.get("name") != null ? (String) response.get("name") : null);
        OAuthDTO.setOrg(response.get("org") != null ? (String) response.get("org") : null);

        return processAuthorization(DataConstants.OAuthProviders.EAIS.getValue(), OAuthDTO);
    }

    @Override
    public String buildAuthRedirect(String redirectUri) {
        String sRedirectUri = buildRedirectUri(null, redirectUri);

        return String.format("%s?response_type=code&scope=read&client_id=%s&redirect_uri=%s", AC_URL, CLIENT_ID, sRedirectUri);
    }

    @Override
    public String buildRedirectUri(String additionalParameters, String redirectUri) {
        String sRedirectUri = redirectUri != null ? redirectUri : getRedirectUrl(DataConstants.OAuthProviders.EAIS.name());
        if (!StringUtils.isEmpty(additionalParameters)) {
            sRedirectUri = sRedirectUri + additionalParameters;
        }
        try {
            sRedirectUri = URLEncoder.encode(sRedirectUri, "UTF-8");
        }
        catch (Exception ignored) {}

        return sRedirectUri;
    }

    private static Map<Object, Object> getQueryWithAuthenticationReturnJson(String url, String authentication) {
        logger.info("Request to: " + url);

        CloseableHttpClient httpClient = HttpClients.custom().build();

        HttpGet method = new HttpGet(url);

        method.addHeader("Authorization", authentication);

        method.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
        method.getParams().setParameter("http.protocol.single-cookie-header", true);

        Map<Object, Object> result = null;
        String response = null;

        MappingJackson2HttpMessageConverter c = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = c.getObjectMapper();

        try {
            CloseableHttpResponse httpResult = httpClient.execute(method);
            logger.info("Request status: " + httpResult);

            HttpEntity entity = httpResult.getEntity();
            if (entity != null) {
                if (httpResult.getStatusLine().getStatusCode() == 200) {
                    response = EntityUtils.toString(entity);
                }
                EntityUtils.consume(entity);
                logger.info("Response body: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                result = objectMapper.readValue(response, new TypeReference<Map<Object, Object>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    private static Map<Object, Object> postQueryWithAuthenticationReturnJson(String url, String authentication) {
        logger.info("Request to: " + url);

        CloseableHttpClient httpClient = HttpClients.custom().build();

        HttpPost method = new HttpPost(url);

        method.addHeader("Authorization", authentication);
        method.addHeader("Accept", "application/json");

        method.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
        method.getParams().setParameter("http.protocol.single-cookie-header", true);

        Map<Object, Object> result = null;
        String response = null;

        MappingJackson2HttpMessageConverter c = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = c.getObjectMapper();

        try {
            CloseableHttpResponse httpResult = httpClient.execute(method);
            logger.info("Request status: " + httpResult);

            HttpEntity entity = httpResult.getEntity();
            if (entity != null) {
                if (httpResult.getStatusLine().getStatusCode() == 200) {
                    response = EntityUtils.toString(entity);
                }
                EntityUtils.consume(entity);
                logger.info("Response body: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                result = objectMapper.readValue(response, new TypeReference<Map<Object, Object>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    @PostConstruct
    public void init() {
        AuthProvider authProvider = idObjectService.getObjectById(AuthProvider.class, DataConstants.OAuthProviders.EAIS.getValue());
        if (authProvider != null) {
            if (authProvider.getAccessTokenEndpoint() != null) {
                ACCESS_TOKEN_ENDPOINT = authProvider.getAccessTokenEndpoint();
                AC_URL = ACCESS_TOKEN_ENDPOINT.replaceAll("(?<=oauth2\\/).+", "ac");
            }
            if (authProvider.getInfoEndpoint() != null) {
                INFO_ENDPOINT = authProvider.getInfoEndpoint();
            }
            CLIENT_ID = authProvider.getClientId();
            CLIENT_SECRET = authProvider.getClientSecret();
        }
    }
}
