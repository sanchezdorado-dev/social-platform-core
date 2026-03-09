package com.socialplatform.core.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.enums.NotificationType;
import com.socialplatform.core.domain.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.read = true, n.readDate = :readDate WHERE n.user.id = :userId AND n.read = false")
    void markAllAsReadByUserId(@Param("userId") UUID userId, @Param("readDate") LocalDateTime readDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.actorUser = NULL WHERE n.actorUser.id = :actorUserId")
    void nullifyActorByActorUserId(@Param("actorUserId") UUID actorUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM Notification n
            WHERE n.type = :type
              AND n.targetId IN (
            SELECT r.id
            FROM Report r
            WHERE r.reporter.id = :userId
               OR r.post.author.id = :userId
              )
            """)
    void deletePendingReportNotificationsRelatedToUser(@Param("userId") UUID userId,
            @Param("type") NotificationType type);

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadFalse(UUID userId, Pageable pageable);

    Optional<Notification> findFirstByUserIdAndTypeAndTargetId(UUID userId, NotificationType type, UUID targetId);

    long countByUserIdAndReadFalse(UUID userId);

    boolean existsByUserIdAndTypeAndTargetId(UUID userId, NotificationType type, UUID targetId);

}
