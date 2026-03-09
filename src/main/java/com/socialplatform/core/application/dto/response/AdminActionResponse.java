package com.socialplatform.core.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialplatform.core.domain.enums.AdminActionType;

public record AdminActionResponse(
        UUID id,
        AdminActionType actionType,
        String description,
        String reason,
        UUID objectId,
        String objectTable,
        UUID adminId,
        String adminUsername,
        String adminRole,
        UUID affectedUserId,
        String affectedUsername,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
}
