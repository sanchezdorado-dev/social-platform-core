package com.socialplatform.core.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialplatform.core.domain.enums.ReactionType;

public record ReactionResponse(
        UUID id,
        ReactionType type,
        UserSummaryResponse user,
        UUID postId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt) {
}
