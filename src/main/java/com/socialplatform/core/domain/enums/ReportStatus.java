package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    PENDING("Report is awaiting review"),
    RESOLVED("Report has been reviewed and resolved"),
    REJECTED("Report has been reviewed and dismissed");

    private final String description;
}
