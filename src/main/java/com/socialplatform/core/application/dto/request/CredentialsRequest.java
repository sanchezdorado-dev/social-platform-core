package com.socialplatform.core.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CredentialsRequest(
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username,

        @Email(message = "Invalid email format") String email,

        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", message = "Password must meet complexity requirements: at least one uppercase letter, one lowercase letter, one digit, and one special character") String password) {
}
