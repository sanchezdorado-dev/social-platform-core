package com.socialplatform.core.application.dto.response;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String occupation,
        String profilePictureUrl) {
}
