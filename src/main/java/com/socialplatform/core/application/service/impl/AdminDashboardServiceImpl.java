package com.socialplatform.core.application.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.socialplatform.core.application.dto.response.AdminActionResponse;
import com.socialplatform.core.application.dto.response.AdminActionTypeCountResponse;
import com.socialplatform.core.application.dto.response.AdminDashboardMetricsResponse;
import com.socialplatform.core.application.dto.response.AdminDashboardResponse;
import com.socialplatform.core.application.dto.response.AdminReportTrendPointResponse;
import com.socialplatform.core.application.mapper.AdminActionMapper;
import com.socialplatform.core.application.service.AdminDashboardService;
import com.socialplatform.core.application.service.AdminRoleResolver;
import com.socialplatform.core.domain.enums.ReportStatus;
import com.socialplatform.core.domain.repository.AdminActionRepository;
import com.socialplatform.core.domain.repository.CommentRepository;
import com.socialplatform.core.domain.repository.PostRepository;
import com.socialplatform.core.domain.repository.ReactionRepository;
import com.socialplatform.core.domain.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final AdminActionRepository adminActionRepository;
    private final AdminActionMapper adminActionMapper;
    private final AdminRoleResolver adminRoleResolver;

    @Override
    public AdminDashboardResponse getDashboard(int trendDays, int activityLimit) {
        int safeTrendDays = Math.max(7, Math.min(trendDays, 60));
        int safeActivityLimit = Math.max(5, Math.min(activityLimit, 50));

        Set<String> visibleAdminRoles = adminRoleResolver.resolveVisibleRoles();

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime last24h = now.minusHours(24);
        LocalDateTime last7d = now.minusDays(7);

        var metrics = new AdminDashboardMetricsResponse(
                reportRepository.countByStatus(ReportStatus.PENDING),
                reportRepository.countCreatedAfter(last24h),
                reportRepository.countByStatusUpdatedAfter(ReportStatus.RESOLVED, last24h),
                reportRepository.countByStatusUpdatedAfter(ReportStatus.REJECTED, last24h),
                countActiveUsersSince(last7d),
                postRepository.countCreatedAfter(last24h),
                postRepository.countSoftDeletedAfter(last7d),
                adminActionRepository.countByAdminRoleInAndCreatedAtGreaterThanEqual(visibleAdminRoles, last24h));

        List<AdminReportTrendPointResponse> reportTrend = buildReportTrend(safeTrendDays);
        List<AdminActionTypeCountResponse> actionTypeDistribution = buildActionTypeDistribution(visibleAdminRoles,
                last7d);
        List<AdminActionResponse> recentActivity = adminActionRepository
                .findByAdminRoleIn(visibleAdminRoles,
                        PageRequest.of(0, safeActivityLimit, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(adminActionMapper::toResponse)
                .getContent();

        return new AdminDashboardResponse(metrics, reportTrend, actionTypeDistribution, recentActivity);
    }

    private long countActiveUsersSince(LocalDateTime since) {
        Set<java.util.UUID> activeUserIds = new HashSet<>();
        activeUserIds.addAll(postRepository.findDistinctActiveAuthorIdsSince(since));
        activeUserIds.addAll(commentRepository.findDistinctAuthorIdsSince(since));
        activeUserIds.addAll(reactionRepository.findDistinctActiveUserIdsSince(since));
        return activeUserIds.size();
    }

    private List<AdminReportTrendPointResponse> buildReportTrend(int days) {
        List<AdminReportTrendPointResponse> trend = new ArrayList<>(days);

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate startDay = today.minusDays(days - 1L);

        for (int offset = 0; offset < days; offset++) {
            LocalDate currentDay = startDay.plusDays(offset);
            LocalDateTime from = currentDay.atStartOfDay();
            LocalDateTime to = currentDay.plusDays(1).atStartOfDay();

            long created = reportRepository.countCreatedBetween(from, to);
            long resolved = reportRepository.countByStatusUpdatedBetween(ReportStatus.RESOLVED, from, to);
            long rejected = reportRepository.countByStatusUpdatedBetween(ReportStatus.REJECTED, from, to);

            trend.add(new AdminReportTrendPointResponse(currentDay, created, resolved, rejected));
        }

        return trend;
    }

    private List<AdminActionTypeCountResponse> buildActionTypeDistribution(Set<String> visibleAdminRoles,
            LocalDateTime since) {
        return adminActionRepository.countByTypeForRolesSince(visibleAdminRoles, since).stream()
                .map(p -> new AdminActionTypeCountResponse(p.getAdminActionType(), p.getCount()))
                .toList();
    }
}
