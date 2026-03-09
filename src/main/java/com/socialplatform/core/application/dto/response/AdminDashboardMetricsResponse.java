package com.socialplatform.core.application.dto.response;

public record AdminDashboardMetricsResponse(
        long pendingReports,
        long reportsCreatedLast24h,
        long reportsResolvedLast24h,
        long reportsRejectedLast24h,
        long activeUsersLast7d,
        long postsCreatedLast24h,
        long contentRemovedLast7d,
        long adminActionsLast24h) {
}
