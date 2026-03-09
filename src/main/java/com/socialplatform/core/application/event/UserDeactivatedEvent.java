package com.socialplatform.core.application.event;

import java.util.UUID;

public record UserDeactivatedEvent(
        UUID userId,
        String username,
        UUID adminId,
        String reason) {
}
