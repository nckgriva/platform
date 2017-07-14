package com.gracelogic.platform.oauth.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.oauth.model.AuthProviderLinkage;
import com.gracelogic.platform.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthServiceImpl implements OAuthService {
    @Autowired
    private IdObjectService idObjectService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAuthProviderLinkages(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        idObjectService.delete(AuthProviderLinkage.class, "el.user.id=:userId", params);
    }
}
