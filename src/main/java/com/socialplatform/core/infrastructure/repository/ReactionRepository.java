package com.socialplatform.core.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.model.Reaction;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Reaction r WHERE r.post.id = :postId")
    void hardDeleteAllByPostId(@Param("postId") UUID postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Reaction r SET r.deletedAt = :deletedAt WHERE r.post.id = :postId AND r.deletedAt IS NULL")
    void softDeleteByPostId(@Param("postId") UUID postId, @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Reaction r SET r.deletedAt = :deletedAt WHERE r.user.id = :userId AND r.deletedAt IS NULL")
    void softDeleteByUserId(@Param("userId") UUID userId, @Param("deletedAt") LocalDateTime deletedAt);

    @Query("SELECT r FROM Reaction r WHERE r.post.id = :postId")
    Page<Reaction> findByPostId(@Param("postId") UUID postId, Pageable pageable);

    @Query("SELECT r FROM Reaction r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Reaction> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT r FROM Reaction r WHERE r.user.id = :userId AND r.post.id = :postId")
    Optional<Reaction> findByUserIdAndPostId(@Param("userId") UUID userId, @Param("postId") UUID postId);

    @Query("SELECT DISTINCT r.user.id FROM Reaction r WHERE r.createdAt >= :createdAfter")
    List<UUID> findDistinctActiveUserIdsSince(@Param("createdAfter") LocalDateTime createdAfter);

    @Query("SELECT r.type AS type, COUNT(r) AS count FROM Reaction r WHERE r.post.id = :postId GROUP BY r.type")
    List<ReactionTypeCountProjection> countReactionsByPostIdGroupedByType(@Param("postId") UUID postId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reaction r WHERE r.user.id = :userId AND r.post.id = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") UUID userId, @Param("postId") UUID postId);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

}
