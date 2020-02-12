package com.gracelogic.platform.user.security;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.IdentifierDTO;
import com.gracelogic.platform.user.exception.TokenExpiredException;
import com.gracelogic.platform.user.exception.TokenNotFoundException;
import com.gracelogic.platform.user.model.*;
import com.gracelogic.platform.user.security.tokenAuth.TokenAuthentication;
import com.gracelogic.platform.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service(value = "defaultAuthenticationProvider")
public class AuthenticationProviderImpl implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Autowired
    private IdObjectService idObjectService;

    @Override
    public org.springframework.security.core.Authentication authenticate(org.springframework.security.core.Authentication authentication) throws AuthenticationException {
        if (authentication instanceof AuthenticationToken) {
            Identifier identifier = userService.processSignIn(((AuthenticationToken) authentication).getIdentifierTypeId(), (String) authentication.getPrincipal(), (String) authentication.getCredentials(), ((AuthenticationToken) authentication).getRemoteAddress());
            if (identifier != null) {
                User user = identifier.getUser();
                AuthorizedUser authorizedUser = AuthorizedUser.prepare(user);
                authorizedUser.setSignInIdentifier(IdentifierDTO.prepare(identifier));
                Set<GrantedAuthority> authorities = commonAuth(user, authorizedUser);
                authentication = new AuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities, ((AuthenticationToken) authentication).getRemoteAddress(), ((AuthenticationToken) authentication).getIdentifierTypeId(), false);

                ((UsernamePasswordAuthenticationToken) authentication).setDetails(authorizedUser);
            } else {
                throw new BadCredentialsException("Invalid identifier or password.");
            }

            return authentication;

        } else {
            TokenAuthentication tokenAuthentication = (TokenAuthentication)authentication;
            String fetches = " left join fetch el.user " +
                    " left join fetch el.identifier ";

            Token token = idObjectService.getObjectById(Token.class, fetches, tokenAuthentication.getToken());
            if (token == null) {
                throw new TokenNotFoundException("Token not found");
            }

            if (!token.getActive()) {
                throw new TokenExpiredException("Token is expired");
            }

            token.setLastRequest(new Date());
            userService.updateTokenInfo(token);

            User user = token.getUser();
            AuthorizedUser authorizedUser = AuthorizedUser.prepare(user);
            authorizedUser.setSignInIdentifier(IdentifierDTO.prepare(token.getIdentifier()));

            Set<GrantedAuthority> authorities = commonAuth(user, authorizedUser);
            tokenAuthentication.setAuthenticated(true);
            tokenAuthentication.setUserDetails(authorizedUser);
            tokenAuthentication.setGrantedAuthorities(authorities);
            return tokenAuthentication;
        }
    }

    private Set<GrantedAuthority> commonAuth(User user, AuthorizedUser authorizedUser) {

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

        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticationToken.class) || authentication.equals(TokenAuthentication.class);
    }
}
