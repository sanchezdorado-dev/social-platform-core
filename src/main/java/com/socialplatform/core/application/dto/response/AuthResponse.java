package com.socialplatform.core.application.dto.response;

public record AuthResponse(
        UserResponse user,
        String accessToken,
        String refreshToken,
        long expiresIn) {
}
