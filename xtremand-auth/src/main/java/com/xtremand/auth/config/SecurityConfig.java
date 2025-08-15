package com.xtremand.auth.config;

import com.xtremand.auth.service.CustomUserDetailsService;
import com.xtremand.shared.services.filter.RequestIdFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, RequestIdFilter requestIdFilter) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/login", "/auth/signup", "/contacts/**", "/ai/**", "/emails/**",
								"/sms/**", "/whatsapp/**","/campaigns/**", "/analytics/campaigns", "/swagger-ui/**", "/swagger-ui.html","/**")
						.permitAll().anyRequest().authenticated())
				.addFilterBefore(requestIdFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	CustomUserDetailsService customUserDetailsService(com.xtremand.user.repository.UserRepository userRepository) {
		return new CustomUserDetailsService(userRepository);
	}

	@Bean
	RequestIdFilter requestIdFilter() {
		return new RequestIdFilter();
	}
}
