package com.socialplatform.core.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.socialplatform.core.domain.enums.UserStatus;

public record UserResponse(
        UUID id,
        String username,
        String email,
        @JsonProperty("role") String roleName,
        UserStatus status,
        ProfileResponse profile,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
}
