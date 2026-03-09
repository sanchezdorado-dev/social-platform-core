package com.socialplatform.core.application.service;

import com.socialplatform.core.application.dto.response.AdminDashboardResponse;

public interface AdminDashboardService {

    AdminDashboardResponse getDashboard(int trendDays, int activityLimit);
}
