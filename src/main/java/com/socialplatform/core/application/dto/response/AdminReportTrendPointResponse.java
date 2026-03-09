package com.socialplatform.core.application.dto.response;

import java.time.LocalDate;

public record AdminReportTrendPointResponse(
        LocalDate date,
        long created,
        long resolved,
        long rejected) {
}
