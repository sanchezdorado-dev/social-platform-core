package com.socialplatform.core.application.dto.request;

import java.util.UUID;

import com.socialplatform.core.domain.enums.ReportReason;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportRequest(
        @NotNull(message = "Reason is mandatory") ReportReason reason,

        @Size(max = 2000, message = "Description too long") String description,

        @NotNull(message = "Post ID to report is mandatory") UUID postId) {
}
