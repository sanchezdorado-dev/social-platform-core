package com.socialplatform.core.application.service;

import com.socialplatform.core.domain.model.Report;
import com.socialplatform.core.domain.model.User;

public interface ReportNotificationFormatter {

    String formatTitle(String reasonRaw);

    String formatMessage(String reporterDisplayName, String optionalDescription);

    String resolveDisplayName(User reporter);

    String resolveDisplayName(Report report);

}
