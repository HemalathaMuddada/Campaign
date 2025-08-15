package com.xtremand.auth.config;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * Extension point for future customisation of the JDBC authorisation service.
 */
public class CustomOAuth2AuthorizationService extends JdbcOAuth2AuthorizationService {

    public CustomOAuth2AuthorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
        super(jdbcOperations, registeredClientRepository);
    }
}
