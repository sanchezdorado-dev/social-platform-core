package com.socialplatform.core.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "Role ID is mandatory") UUID roleId) {
}
