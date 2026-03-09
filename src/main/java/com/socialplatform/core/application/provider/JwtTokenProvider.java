package com.socialplatform.core.application.provider;

import java.time.Instant;

import com.socialplatform.core.domain.model.User;

public interface JwtTokenProvider {

    String generateAccessToken(User user);

    String generateRefreshToken(User user, String jti);

    long getAccessTokenValidity();

    Instant getRefreshTokenExpiryInstant();
}
