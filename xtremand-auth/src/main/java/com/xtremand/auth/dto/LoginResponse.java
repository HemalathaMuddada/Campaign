package com.xtremand.auth.dto;

import com.xtremand.common.dto.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UserProfile user;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
