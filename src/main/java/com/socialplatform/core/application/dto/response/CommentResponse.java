package com.socialplatform.core.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CommentResponse(
        UUID id,
        String content,
        UserSummaryResponse author,
        UUID postId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
}
