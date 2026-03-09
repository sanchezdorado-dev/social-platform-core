package com.socialplatform.core.application.event;

import java.util.UUID;

public record AdminPrivilegesRevokedEvent(
        UUID adminId,
        String adminUsername,
        UUID executorAdminId,
        String reason) {
}
