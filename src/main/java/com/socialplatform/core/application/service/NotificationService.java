package com.socialplatform.core.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.response.NotificationResponse;
import com.socialplatform.core.domain.enums.NotificationType;

public interface NotificationService {

    Page<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(UUID userId, Pageable pageable);

    void create(UUID userId, UUID actorUserId, String title, String message, NotificationType type, UUID targetId);

    void createSafely(UUID userId, UUID actorUserId, String title, String message, NotificationType type,
            UUID targetId);

    void upsertSafely(UUID userId, UUID actorUserId, String title, String message, NotificationType type,
            UUID targetId);

    long countUnread(UUID userId);

    void markAsRead(UUID id, UUID userId);

    void markAllAsRead(UUID userId);

    void delete(UUID id, UUID userId);
}
