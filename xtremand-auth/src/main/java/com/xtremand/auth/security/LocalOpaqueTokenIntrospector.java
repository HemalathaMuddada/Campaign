package com.xtremand.auth.security;

import com.xtremand.auth.service.TokenInfo;
import com.xtremand.auth.service.TokenService;
import com.xtremand.domain.entity.User;
import com.xtremand.user.repository.UserRepository;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public LocalOpaqueTokenIntrospector(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        TokenInfo info = tokenService.get(token);
        if (info == null || !tokenService.isValid(token)) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"));
        }
        User user = userRepository.findByEmail(info.getUsername())
                .orElseThrow(() -> new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id", user.getId());
        attributes.put("email", user.getEmail());
        String role = user.getRole() != null ? user.getRole().getName().name() : "USER";
        return new DefaultOAuth2AuthenticatedPrincipal(attributes,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}
