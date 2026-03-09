package com.socialplatform.core.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.request.ReportRequest;
import com.socialplatform.core.application.dto.response.ReportResponse;
import com.socialplatform.core.domain.enums.ReportStatus;

public interface ReportService {

    Page<ReportResponse> getByStatus(ReportStatus status, Pageable pageable);

    ReportResponse create(ReportRequest request, UUID reporterId);

    ReportResponse getById(UUID id);

    ReportResponse updateStatus(UUID id, ReportStatus status, UUID adminId);

    long countPending();
}
