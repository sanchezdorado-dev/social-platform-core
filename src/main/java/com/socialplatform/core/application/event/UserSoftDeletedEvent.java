package com.socialplatform.core.application.event;

import java.util.UUID;

public record UserSoftDeletedEvent(
        UUID userId,
        String username,
        UUID adminId,
        String reason) {
}
