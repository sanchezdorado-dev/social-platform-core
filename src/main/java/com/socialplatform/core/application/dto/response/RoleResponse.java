package com.socialplatform.core.application.dto.response;

import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        boolean defaultRole) {
}
