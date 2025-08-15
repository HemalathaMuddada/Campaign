package com.xtremand.auth.config;

import com.xtremand.auth.security.LocalOpaqueTokenIntrospector;
import com.xtremand.auth.service.TokenService;
import com.xtremand.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceServerConfig {

    @Bean
    @Order(2)
    SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http, OpaqueTokenIntrospector introspector) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(ot -> ot.introspector(introspector)));
        return http.build();
    }

    @Bean
    OpaqueTokenIntrospector opaqueTokenIntrospector(TokenService tokenService, UserRepository userRepository) {
        return new LocalOpaqueTokenIntrospector(tokenService, userRepository);
    }
}
