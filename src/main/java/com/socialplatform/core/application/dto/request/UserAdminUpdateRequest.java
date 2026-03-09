package com.socialplatform.core.application.dto.request;

import com.socialplatform.core.domain.enums.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserAdminUpdateRequest(
        @NotBlank(message = "Username is required") @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters") String username,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters") @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,49}$", message = "Role name must be uppercase and may only contain letters, digits, and underscores") String role,

        UserStatus status) {
}
