package com.socialplatform.core.infrastructure.repository;

import com.socialplatform.core.domain.enums.AdminActionType;

public interface AdminActionTypeCountProjection {

    AdminActionType getAdminActionType();

    Long getCount();

}
