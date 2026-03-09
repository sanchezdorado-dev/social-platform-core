package com.socialplatform.core.application.service.impl;

import org.springframework.stereotype.Service;

import com.socialplatform.core.application.service.ReportNotificationFormatter;
import com.socialplatform.core.domain.model.Report;
import com.socialplatform.core.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportNotificationFormatterImpl implements ReportNotificationFormatter {

    @Override
    public String formatTitle(String reasonRaw) {
        return "Reporte por " + toReadableReason(reasonRaw);
    }

    @Override
    public String formatMessage(String reporterDisplayName, String optionalDescription) {
        String safeReporter = (reporterDisplayName == null || reporterDisplayName.isBlank())
                ? "Alguien"
                : reporterDisplayName.trim();

        String description = normalizeOptionalText(optionalDescription);
        if (description == null) {
            return safeReporter + " reportó.";
        }

        return safeReporter + " reportó: " + description;
    }

    @Override
    public String resolveDisplayName(User reporter) {
        if (reporter.getProfile() != null) {
            String firstName = reporter.getProfile().getFirstName() != null
                    ? reporter.getProfile().getFirstName().trim()
                    : "";
            String lastName = reporter.getProfile().getLastName() != null ? reporter.getProfile().getLastName().trim()
                    : "";
            String fullName = (firstName + " " + lastName).trim();
            if (!fullName.isEmpty()) {
                return fullName;
            }
        }
        return reporter.getUsername();
    }

    @Override
    public String resolveDisplayName(Report report) {
        if (report.getReporter() == null) {
            return "Alguien";
        }
        return resolveDisplayName(report.getReporter());
    }

    private String toReadableReason(String reasonRaw) {
        if (reasonRaw == null || reasonRaw.isBlank()) {
            return "Otro";
        }

        return switch (reasonRaw.trim().toUpperCase()) {
            case "SPAM" -> "Spam";
            case "HATE_SPEECH" -> "Discurso de Odio";
            case "SELF_HARM" -> "Autolesión";
            case "INAPPROPRIATE_CONTENT" -> "Contenido Inapropiado";
            case "HARASSMENT" -> "Acoso";
            case "FALSE_INFORMATION" -> "Información Falsa";
            case "ILLEGAL_ACTIVITY" -> "Actividad Ilegal";
            default -> "Otro";
        };
    }

    private String normalizeOptionalText(String text) {
        if (text == null) {
            return null;
        }

        String normalized = text.trim();
        return normalized.isEmpty() ? null : normalized;
    }

}
