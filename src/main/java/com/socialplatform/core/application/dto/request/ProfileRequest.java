package com.socialplatform.core.application.dto.request;

import java.time.LocalDate;

import com.socialplatform.core.domain.enums.AccountVisibility;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @Size(max = 50, message = "First name must not exceed 50 characters") String firstName,

        @Size(max = 50, message = "Last name must not exceed 50 characters") String lastName,

        AccountVisibility accountVisibility,

        @Size(max = 500, message = "Bio must not exceed 500 characters") String bio,

        @Size(max = 20, message = "Phone number is too long") @Pattern(regexp = "^\\+?[0-9]*$", message = "Phone number format is invalid") String phoneNumber,

        @Past(message = "Birth date must be in the past") LocalDate birthDate,

        @Size(max = 100, message = "Occupation description is too long") String occupation,

        @Size(max = 1000, message = "Interests description is too long") String interests,

        @Size(max = 255, message = "Website URL is too long") @Pattern(regexp = "^(https?://.*)?$", message = "Website must be a valid URL") String website,

        @Size(max = 150, message = "Location description is too long") String location,

        @Size(max = 255, message = "Education details are too long") String education,

        @Size(max = 255, message = "Workplace details are too long") String workplace) {
}
