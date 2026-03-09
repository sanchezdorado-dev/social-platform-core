package com.socialplatform.core.application.event;

import java.util.UUID;

public record ReportRejectedEvent(
        UUID reportId,
        UUID reporterId,
        UUID postId,
        UUID executorAdminId,
        String reason) {
}
