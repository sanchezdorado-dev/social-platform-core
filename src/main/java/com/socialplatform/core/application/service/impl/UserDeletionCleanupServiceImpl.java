package com.socialplatform.core.application.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.service.UserDeletionCleanupService;
import com.socialplatform.core.domain.repository.CommentRepository;
import com.socialplatform.core.domain.repository.FollowRepository;
import com.socialplatform.core.domain.repository.NotificationRepository;
import com.socialplatform.core.domain.repository.ReactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeletionCleanupServiceImpl implements UserDeletionCleanupService {

    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void cleanupForSoftDeletedUser(UUID userId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        followRepository.deleteAllByUserId(userId);
        notificationRepository.deleteAllByUserId(userId);
        notificationRepository.nullifyActorByActorUserId(userId);
        reactionRepository.softDeleteByUserId(userId, now);
        commentRepository.softDeleteByAuthorId(userId, now);
    }
}
