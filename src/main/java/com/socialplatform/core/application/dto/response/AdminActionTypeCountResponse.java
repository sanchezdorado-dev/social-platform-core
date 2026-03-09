package com.socialplatform.core.application.dto.response;

import com.socialplatform.core.domain.enums.AdminActionType;

public record AdminActionTypeCountResponse(
        AdminActionType actionType,
        long count) {
}
