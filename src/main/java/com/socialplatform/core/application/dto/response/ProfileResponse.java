package com.socialplatform.core.application.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialplatform.core.domain.enums.AccountVisibility;

public record ProfileResponse(
        String firstName,
        String lastName,
        AccountVisibility accountVisibility,
        String bio,
        String occupation,
        String interests,
        String website,
        String location,
        String education,
        String workplace,
        String profilePictureUrl,
        String coverPictureUrl,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate) {
}
