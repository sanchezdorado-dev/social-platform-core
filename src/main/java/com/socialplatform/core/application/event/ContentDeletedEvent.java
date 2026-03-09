package com.socialplatform.core.application.event;

import java.util.UUID;

public record ContentDeletedEvent(
        UUID postId,
        UUID postAuthorId,
        UUID executorAdminId,
        String reason) {
}
