package com.socialplatform.core.application.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is mandatory") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,

        @NotBlank(message = "Last name is mandatory") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,

        @NotBlank(message = "Username is mandatory") @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters") @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Username must be alphanumeric and may only contain dots or underscores") String username,

        @NotBlank(message = "Email address is mandatory") @Email(message = "Provided email address must follow a valid RFC 5322 format") @Size(max = 100, message = "Email address exceeds maximum allowed length of 100 characters") String email,

        @NotBlank(message = "Password is mandatory") @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", message = "Password must meet complexity requirements: at least one uppercase letter, one lowercase letter, one digit, and one special character") String password,

        @NotNull(message = "Birth date is mandatory") @Past(message = "Birth date must be a valid chronological date in the past") LocalDate birthDate) {
}
