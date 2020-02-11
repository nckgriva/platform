package com.gracelogic.platform.user.security.tokenAuth;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.IdentifierDTO;
import com.gracelogic.platform.user.exception.TokenExpiredException;
import com.gracelogic.platform.user.exception.TokenNotFoundException;
import com.gracelogic.platform.user.model.*;
import com.gracelogic.platform.user.security.AuthenticationToken;
import com.gracelogic.platform.user.service.DataConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service(value = "tokenAuthenticationProvider")
public class TokenAuthenticationProviderImpl implements AuthenticationProvider {

    @Autowired
    private IdObjectService idObjectService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TokenAuthentication tokenAuthentication = (TokenAuthentication)authentication;
        String fetches = " left join fetch el.tokenStatus " +
                " left join fetch el.user " +
                " left join fetch el.identifier ";

        Token token = idObjectService.getObjectById(Token.class, fetches, tokenAuthentication.getToken());
        if (token == null) {
            throw new TokenNotFoundException("Token not found");
        }

        if (!token.getActive()) {
            throw new TokenExpiredException("Token is expired");
        }

        token.setLastRequest(new Date());
        idObjectService.save(token);

        User user = token.getUser();
        AuthorizedUser authorizedUser = AuthorizedUser.prepare(user);
        authorizedUser.setSignInIdentifier(IdentifierDTO.prepare(token.getIdentifier()));

        //Load roles & grants
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        List<UserRole> roles = idObjectService.getList(UserRole.class, null, "el.user.id=:userId", params, null, null, null, null);
        Set<UUID> roleIds = new HashSet<>();
        for (UserRole ur : roles) {
            roleIds.add(ur.getRole().getId());
        }
        params = new HashMap<>();
        params.put("roleIds", roleIds);

        List<RoleGrant> roleGrants = idObjectService.getList(RoleGrant.class, "left join fetch el.grant", "el.role.id in :roleIds", params, null, null, null, null);

        //Set grants
        Set<Grant> grants = new HashSet<Grant>();
        for (RoleGrant roleGrant : roleGrants) {
            grants.add(roleGrant.getGrant());
        }

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (Grant grant : grants) {
            authorities.add(new SimpleGrantedAuthority(grant.getCode()));
            authorizedUser.getGrants().add(grant.getCode());
        }

        tokenAuthentication.setAuthenticated(true);
        tokenAuthentication.setUserDetails(authorizedUser);
        tokenAuthentication.setGrantedAuthorities(authorities);


        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthentication.class.equals(authentication);
    }

}
