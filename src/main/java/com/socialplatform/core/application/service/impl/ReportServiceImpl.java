package com.socialplatform.core.application.service.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.request.ReportRequest;
import com.socialplatform.core.application.dto.response.ReportResponse;
import com.socialplatform.core.application.event.ReportCreatedEvent;
import com.socialplatform.core.application.event.ReportRejectedEvent;
import com.socialplatform.core.application.event.ReportResolvedEvent;
import com.socialplatform.core.application.exception.ReopenFinishedReportException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.mapper.ReportMapper;
import com.socialplatform.core.application.service.PostService;
import com.socialplatform.core.application.service.ReportNotificationFormatter;
import com.socialplatform.core.application.service.ReportService;
import com.socialplatform.core.domain.enums.ReportStatus;
import com.socialplatform.core.domain.model.Post;
import com.socialplatform.core.domain.model.Report;
import com.socialplatform.core.domain.model.User;
import com.socialplatform.core.domain.repository.PostRepository;
import com.socialplatform.core.domain.repository.ReportRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final ReportMapper reportMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ReportNotificationFormatter reportFormatter;

    @Override
    @Transactional
    public ReportResponse create(ReportRequest request, UUID reporterId) {
        User reporter = userRepository.findActiveById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));

        Post post = null;
        if (request.postId() != null) {
            post = postRepository.findById(request.postId())
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + request.postId()));
        }

        var report = Report.builder()
                .reporter(reporter)
                .reason(request.reason())
                .description(request.description())
                .post(post)
                .build();

        Report savedReport = reportRepository.save(report);

        eventPublisher.publishEvent(new ReportCreatedEvent(
                savedReport.getId(),
                reporterId,
                savedReport.getPost() != null ? savedReport.getPost().getId() : null,
                reportFormatter.resolveDisplayName(reporter),
                request.reason().name(),
                request.description()));

        return reportMapper.toResponse(savedReport);
    }

    @Override
    public ReportResponse getById(UUID id) {
        return reportRepository.findActiveById(id)
                .map(reportMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + id));
    }

    @Override
    public Page<ReportResponse> getByStatus(ReportStatus status, Pageable pageable) {
        if (status == null) {
            return reportRepository.findAllActive(pageable)
                    .map(reportMapper::toResponse);
        }
        return reportRepository.findByStatus(status, pageable)
                .map(reportMapper::toResponse);
    }

    @Override
    @Transactional
    public ReportResponse updateStatus(UUID id, ReportStatus status, UUID adminId) {
        Report report = reportRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        if (report.getStatus() == status) {
            return reportMapper.toResponse(report);
        }

        if (report.getStatus() != ReportStatus.PENDING && status == ReportStatus.PENDING) {
            throw new ReopenFinishedReportException("Cannot re-open a finished report");
        }

        report.transitionTo(status);
        UUID postId = report.getPost() != null ? report.getPost().getId() : null;

        if (status == ReportStatus.RESOLVED && postId != null) {
            postService.softDeleteWithRelatedContent(postId, adminId);

            reportRepository.resolveAllPendingReportsForPost(postId, ReportStatus.PENDING, ReportStatus.RESOLVED);

            eventPublisher.publishEvent(new ReportResolvedEvent(
                    id,
                    report.getReporter().getId(),
                    postId,
                    adminId,
                    "Content violated community guidelines"));

        } else if (status == ReportStatus.REJECTED && postId != null) {
            eventPublisher.publishEvent(new ReportRejectedEvent(
                    id,
                    report.getReporter().getId(),
                    postId,
                    adminId,
                    "Report reviewed and rejected"));
        }

        Report updated = reportRepository.save(report);

        return reportMapper.toResponse(updated);
    }

    @Override
    public long countPending() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }
}
