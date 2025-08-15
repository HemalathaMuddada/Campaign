package com.xtremand.auth.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Holds authentication token information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    private String token;
    private String username;
    private Instant expiresAt;
}
