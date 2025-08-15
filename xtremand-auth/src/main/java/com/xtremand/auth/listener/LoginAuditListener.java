package com.xtremand.auth.listener;

import com.xtremand.domain.entity.LockoutEvent;
import com.xtremand.domain.enums.LockoutEventType;
import com.xtremand.user.repository.LockoutEventRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginAuditListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private final LockoutEventRepository repository;

    public LoginAuditListener(LockoutEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            record(event.getAuthentication().getName(), LockoutEventType.SUCCESS);
        } else if (event instanceof AbstractAuthenticationFailureEvent failure) {
            record(failure.getAuthentication().getName(), LockoutEventType.FAILURE);
        }
    }

    private void record(String email, LockoutEventType type) {
        LockoutEvent e = LockoutEvent.builder()
                .email(email)
                .eventType(type)
                .build();
        repository.save(e);
    }
}
