package com.socialplatform.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordRequest(
        @NotBlank(message = "Current password is required") String oldPassword,

        @NotBlank(message = "New password is mandatory") @Size(min = 8, max = 64, message = "New password must be between 8 and 64 characters") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", message = "New password does not meet complexity requirements") String newPassword) {
}
