package com.socialplatform.core.application.event;

import java.util.UUID;

public record ReportResolvedEvent(
        UUID reportId,
        UUID reporterId,
        UUID postId,
        UUID executorAdminId,
        String reason) {
}
