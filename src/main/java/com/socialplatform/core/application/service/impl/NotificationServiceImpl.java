package com.socialplatform.core.application.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.response.NotificationResponse;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.exception.UnauthorizedOperationException;
import com.socialplatform.core.application.mapper.NotificationMapper;
import com.socialplatform.core.application.service.NotificationService;
import com.socialplatform.core.domain.enums.NotificationType;
import com.socialplatform.core.domain.model.Notification;
import com.socialplatform.core.domain.repository.NotificationRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void create(UUID userId, UUID actorUserId, String title, String message, NotificationType type,
            UUID targetId) {
        var notification = Notification.builder()
                .user(userRepository.getReferenceById(userId))
                .actorUser(actorUserId != null ? userRepository.getReferenceById(actorUserId) : null)
                .title(title)
                .message(message)
                .type(type)
                .targetId(targetId)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSafely(UUID userId, UUID actorUserId, String title, String message, NotificationType type,
            UUID targetId) {
        try {
            create(userId, actorUserId, title, message, type, targetId);
        } catch (Exception e) {

            log.error("Failed to create notification for user {}. Title: {}, Message: {}",
                    userId, title, message, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upsertSafely(UUID userId, UUID actorUserId, String title, String message, NotificationType type,
            UUID targetId) {
        try {
            notificationRepository.findFirstByUserIdAndTypeAndTargetId(userId, type, targetId)
                    .ifPresentOrElse(
                            existing -> {
                                existing.resetReadStatus();
                                existing.setMessage(message);
                                existing.setActorUser(actorUserId != null
                                        ? userRepository.getReferenceById(actorUserId)
                                        : null);
                                notificationRepository.save(existing);
                            },
                            () -> create(userId, actorUserId, title, message, type, targetId));
        } catch (Exception e) {
            log.error("Failed to upsert notification for user {}. Title: {}, Message: {}",
                    userId, title, message, e);
        }
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public Page<NotificationResponse> getUnreadNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndReadFalse(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(UUID id, UUID userId) {
        var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("You cannot mark as read a notification that is not yours");
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now(ZoneOffset.UTC));
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("Permission denied to delete this notification");
        }

        notificationRepository.delete(notification);
    }
}
