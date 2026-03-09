package com.socialplatform.core.domain.repository;

import com.socialplatform.core.domain.enums.AdminActionType;

public interface AdminActionTypeCountProjection {

    AdminActionType getAdminActionType();

    Long getCount();

}
