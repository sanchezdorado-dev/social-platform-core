package com.socialplatform.core.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialplatform.core.domain.enums.ReportReason;
import com.socialplatform.core.domain.enums.ReportStatus;

public record ReportResponse(
        UUID id,
        ReportReason reason,
        String description,
        ReportStatus status,
        UserSummaryResponse reporter,
        UUID postId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
}
