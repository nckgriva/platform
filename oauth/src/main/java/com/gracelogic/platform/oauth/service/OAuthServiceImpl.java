package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.oauth.dto.AuthProviderDTO;
import com.gracelogic.platform.oauth.model.AuthProvider;
import com.gracelogic.platform.oauth.model.AuthProviderLinkage;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class OAuthServiceImpl implements OAuthService {
    @Autowired
    private IdObjectService idObjectService;

    @Qualifier("vk")
    @Autowired
    private OAuthServiceProvider vk;

    @Qualifier("ok")
    @Autowired
    private OAuthServiceProvider ok;

    @Qualifier("instagram")
    @Autowired
    private OAuthServiceProvider instagram;

    @Qualifier("facebook")
    @Autowired
    private OAuthServiceProvider facebook;

    @Qualifier("google")
    @Autowired
    private OAuthServiceProvider google;

    @Qualifier("linkedin")
    @Autowired
    private OAuthServiceProvider linkedin;

    @Qualifier("esia")
    @Autowired
    private OAuthServiceProvider esia;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAuthProviderLinkages(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        idObjectService.delete(AuthProviderLinkage.class, "el.user.id=:userId", params);
    }

    @Override
    public List<AuthProviderDTO> getAuthProviders() {
        List<AuthProvider> providers = idObjectService.getList(AuthProvider.class);
        List<AuthProviderDTO> dtos = new LinkedList<>();
        for (AuthProvider provider : providers) {
            AuthProviderDTO dto = AuthProviderDTO.prepare(provider);
            if (StringUtils.equalsIgnoreCase(provider.getName(), "VK")) {
                dto.setUrl(vk.buildAuthRedirect());
            }
            else if (StringUtils.equalsIgnoreCase(provider.getName(), "OK")) {
                dto.setUrl(ok.buildAuthRedirect());
            }
            else if (StringUtils.equalsIgnoreCase(provider.getName(), "INSTAGRAM")) {
                dto.setUrl(instagram.buildAuthRedirect());
            }
            else if (StringUtils.equalsIgnoreCase(provider.getName(), "FACEBOOK")) {
                dto.setUrl(facebook.buildAuthRedirect());
            }
            else if (StringUtils.equalsIgnoreCase(provider.getName(), "GOOGLE")) {
                dto.setUrl(google.buildAuthRedirect());
            }
            else if (StringUtils.equalsIgnoreCase(provider.getName(), "LINKEDIN")) {
                dto.setUrl(linkedin.buildAuthRedirect());
            }
            else if (StringUtils.equalsIgnoreCase(provider.getName(), "ESIA")) {
                dto.setUrl(esia.buildAuthRedirect());
            }

            dtos.add(dto);
        }

        return dtos;
    }
}
