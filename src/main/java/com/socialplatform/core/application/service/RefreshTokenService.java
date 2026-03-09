package com.socialplatform.core.application.service;

import java.time.Instant;
import java.util.UUID;

import com.socialplatform.core.domain.model.User;

public interface RefreshTokenService {

    void registerNewToken(User user, String rawToken, String jti, Instant expiresAt);

    void validateAndRotate(String rawToken, String jti, UUID userId);

    void revokeByRawToken(String rawToken);
}
