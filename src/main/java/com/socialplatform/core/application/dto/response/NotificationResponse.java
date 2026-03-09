package com.socialplatform.core.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.socialplatform.core.domain.enums.NotificationType;

public record NotificationResponse(
        UUID id,
        String title,
        String message,
        NotificationType type,
        UserSummaryResponse actor,
        UUID targetId,
        @JsonProperty("isRead") boolean isRead,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
}
