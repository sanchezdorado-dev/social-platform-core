package com.socialplatform.core.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.request.AdminActionRequest;
import com.socialplatform.core.application.dto.response.AdminActionResponse;
import com.socialplatform.core.domain.enums.AdminActionType;

public interface AdminActionService {

    Page<AdminActionResponse> getAuditLog(Pageable pageable);

    Page<AdminActionResponse> getLogByAdmin(UUID adminId, Pageable pageable);

    Page<AdminActionResponse> getLogByType(AdminActionType actionType, Pageable pageable);

    AdminActionResponse executeAndLog(AdminActionRequest request, UUID adminId, String adminUsername);

    AdminActionResponse getById(UUID id);
}
