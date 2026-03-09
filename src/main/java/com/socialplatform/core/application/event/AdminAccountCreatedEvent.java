package com.socialplatform.core.application.event;

import java.util.UUID;

public record AdminAccountCreatedEvent(
        UUID newAdminId,
        String newAdminUsername,
        UUID creatorAdminId,
        String reason) {
}
