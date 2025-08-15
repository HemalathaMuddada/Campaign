package com.xtremand.auth.config;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * Simple wrapper around the Jdbc implementation so we can plug in custom TTL logic later.
 */
public class TokenTtlRegisteredClientRepository implements RegisteredClientRepository {

    private final RegisteredClientRepository delegate;

    public TokenTtlRegisteredClientRepository(RegisteredClientRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        delegate.save(registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return delegate.findByClientId(clientId);
    }
}
