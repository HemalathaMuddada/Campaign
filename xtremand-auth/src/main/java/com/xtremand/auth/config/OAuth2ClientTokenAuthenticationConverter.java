package com.xtremand.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * Extracts client credentials from token requests.
 */
public class OAuth2ClientTokenAuthenticationConverter implements AuthenticationConverter {

    @Override
    public UsernamePasswordAuthenticationToken convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!"client_credentials".equals(grantType)) {
            return null;
        }
        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        String clientSecret = request.getParameter(OAuth2ParameterNames.CLIENT_SECRET);
        if (clientId == null || clientSecret == null) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(clientId, clientSecret);
    }
}
