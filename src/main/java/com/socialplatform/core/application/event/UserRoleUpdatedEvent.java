package com.socialplatform.core.application.event;

import java.util.UUID;

public record UserRoleUpdatedEvent(
        UUID userId,
        String username,
        UUID adminId,
        String oldRoleName,
        String newRoleName,
        String reason) {
}
