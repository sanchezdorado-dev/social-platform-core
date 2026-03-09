package com.socialplatform.core.application.event;

import java.util.UUID;

public record ReportCreatedEvent(
        UUID reportId,
        UUID reporterId,
        UUID postId,
        String reporterDisplayName,
        String reason,
        String description) {
}
