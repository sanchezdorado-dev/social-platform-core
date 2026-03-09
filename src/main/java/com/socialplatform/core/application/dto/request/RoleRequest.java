package com.socialplatform.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @NotBlank(message = "Role name is required") @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters") String name,

        @Size(max = 200, message = "Description cannot exceed 200 characters") String description,

        Boolean isDefault) {
}
