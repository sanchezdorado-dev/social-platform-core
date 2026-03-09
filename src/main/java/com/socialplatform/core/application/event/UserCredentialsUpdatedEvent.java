package com.socialplatform.core.application.event;

import java.util.UUID;

public record UserCredentialsUpdatedEvent(
        UUID userId,
        String username,
        UUID adminId,
        String reason,
        String changedField) {
}
