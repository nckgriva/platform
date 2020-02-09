package com.gracelogic.platform.user.security;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.IdentifierDTO;
import com.gracelogic.platform.user.model.*;
import com.gracelogic.platform.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service(value = "defaultAuthenticationProvider")
public class AuthenticationProviderImpl implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Override
    public org.springframework.security.core.Authentication authenticate(org.springframework.security.core.Authentication authentication) throws AuthenticationException {
        if (authentication instanceof AuthenticationToken) {
            Identifier identifier = userService.processSignIn(((AuthenticationToken) authentication).getIdentifierTypeId(), (String) authentication.getPrincipal(), (String) authentication.getCredentials(), ((AuthenticationToken) authentication).getRemoteAddress());
            if (identifier != null) {
                User user = identifier.getUser();
                AuthorizedUser authorizedUser = AuthorizedUser.prepare(user);
                authorizedUser.setSignInIdentifier(IdentifierDTO.prepare(identifier));

                Set<Grant> grants = new HashSet<Grant>();
                for (UserRole userRole : user.getUserRoles()) {
                    for (RoleGrant roleGrant : userRole.getRole().getRoleGrants()) {
                        grants.add(roleGrant.getGrant());
                    }
                }

                Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
                for (Grant grant : grants) {
                    authorities.add(new SimpleGrantedAuthority(grant.getCode()));

                    authorizedUser.getGrants().add(grant.getCode());
                }
                authentication = new AuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities, ((AuthenticationToken) authentication).getRemoteAddress(), ((AuthenticationToken) authentication).getIdentifierTypeId(), false);

                ((UsernamePasswordAuthenticationToken) authentication).setDetails(authorizedUser);
            }
            else {
                throw new BadCredentialsException("Invalid identifier or password.");
            }
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticationToken.class);
    }
}
