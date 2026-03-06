package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    NEW_FOLLOWER("A user started following you"),
    NEW_COMMENT("Someone commented on your post"),
    POST_REACTION("Someone reacted to your post"),
    REPORT_PENDING("A new report is awaiting admin review"),
    REPORT_RESOLUTION("Your report has been resolved"),
    ACCOUNT_STATUS_CHANGED("Your account status has been updated"),
    ROLE_CHANGED("Your role permissions have been updated"),
    SECURITY_ALERT("A security event was detected on your account"),
    CONTENT_DELETED("Your content has been removed by an administrator");

    private final String description;
}
