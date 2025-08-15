package com.xtremand.auth.service;

import com.xtremand.auth.dto.LoginRequest;
import com.xtremand.auth.dto.LoginResponse;
import com.xtremand.common.util.AESUtil;
import com.xtremand.common.dto.UserProfile;
import com.xtremand.user.repository.UserRepository;
import com.xtremand.domain.entity.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String secretKey;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUri;

    public AuthService(AuthenticationManager authenticationManager,
                       TokenService tokenService,
                       UserRepository userRepository,
                       @Value("${app.auth.secret-key}") String secretKey,
                       @Value("${app.oauth.client-id}") String clientId,
                       @Value("${app.oauth.client-secret}") String clientSecret,
                       @Value("${app.oauth.token-uri}") String tokenUri) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.secretKey = secretKey;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUri = tokenUri;
    }

    public LoginResponse login(LoginRequest request) {
        String decrypted = AESUtil.decrypt(request.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), decrypted)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("username", request.getEmail());
        form.add("password", decrypted);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        @SuppressWarnings("unchecked")
        var response = restTemplate.postForObject(tokenUri, entity, java.util.Map.class);

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        UserProfile profile = UserProfile.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().getName().name() : null)
                .build();

        return LoginResponse.builder()
                .user(profile)
                .accessToken((String) response.get("access_token"))
                .refreshToken((String) response.get("refresh_token"))
                .expiresIn(Long.parseLong(String.valueOf(response.get("expires_in"))))
                .build();
    }
}
