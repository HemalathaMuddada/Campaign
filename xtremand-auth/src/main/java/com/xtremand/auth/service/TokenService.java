package com.xtremand.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();
    private final long expireSeconds;

    public TokenService(@Value("${app.auth.token.expire-seconds:3600}") long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public TokenInfo generate(String username) {
        String token = UUID.randomUUID().toString();
        Instant expires = Instant.now().plusSeconds(expireSeconds);
        TokenInfo info = new TokenInfo(token, username, expires);
        tokens.put(token, info);
        return info;
    }

    public boolean isValid(String token) {
        TokenInfo info = tokens.get(token);
        return info != null && info.getExpiresAt().isAfter(Instant.now());
    }

    public void revoke(String token) {
        tokens.remove(token);
    }

    public TokenInfo get(String token) {
        return tokens.get(token);
    }

    // Token details are defined in TokenInfo.java
}
