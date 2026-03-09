package com.socialplatform.core.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialplatform.core.domain.enums.PostVisibility;

public record PostResponse(
        UUID id,
        String content,
        String imageUrl,
        PostVisibility postVisibility,
        UserSummaryResponse author,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt) {
}
