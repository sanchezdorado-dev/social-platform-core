package com.socialplatform.core.application.dto.request;

import java.util.UUID;

import com.socialplatform.core.domain.enums.AdminActionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminActionRequest(
        @NotNull(message = "Action type is mandatory") AdminActionType adminActionType,

        @NotBlank(message = "Action description is mandatory") @Size(max = 500, message = "Description cannot exceed 500 characters") String description,

        @Size(max = 1000, message = "Reason cannot exceed 1000 characters") String reason,

        @NotNull(message = "Affected object ID is mandatory") UUID objectId,

        @NotBlank(message = "Object table name is mandatory") @Size(max = 100, message = "Table name is too long") String objectTable,

        UUID affectedUserId,

        @Size(max = 50, message = "Affected username is too long") String affectedUsername) {
}
