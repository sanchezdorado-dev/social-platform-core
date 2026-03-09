package com.socialplatform.core.application.dto.response;

import java.util.List;

public record AdminDashboardResponse(
        AdminDashboardMetricsResponse metrics,
        List<AdminReportTrendPointResponse> reportTrend,
        List<AdminActionTypeCountResponse> actionTypeDistribution,
        List<AdminActionResponse> recentActivity) {
}
