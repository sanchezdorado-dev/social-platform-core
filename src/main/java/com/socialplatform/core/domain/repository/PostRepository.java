package com.socialplatform.core.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.enums.AccountVisibility;
import com.socialplatform.core.domain.enums.PostVisibility;
import com.socialplatform.core.domain.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("""
                SELECT p FROM Post p
                WHERE (
                    p.author.id = :currentUserId
                    OR (
                        p.author.id IN :followedIds
                        AND p.visibilityType = :publicVisibility
                        AND p.author.profile.accountVisibility = :publicAccount
                    )
                )
                ORDER BY p.createdAt DESC
            """)
    Page<Post> findPersonalFeed(
            @Param("followedIds") List<UUID> followedIds,
            @Param("currentUserId") UUID currentUserId,
            @Param("publicVisibility") PostVisibility publicVisibility,
            @Param("publicAccount") AccountVisibility publicAccount,
            Pageable pageable);

    @Query("""
                SELECT p FROM Post p
                WHERE p.author.id = :targetUserId
                AND p.visibilityType = :publicVisibility
                AND p.author.profile.accountVisibility = :publicAccount
                ORDER BY p.createdAt DESC
            """)
    Page<Post> findPublicPostsByUser(
            @Param("targetUserId") UUID targetUserId,
            @Param("publicVisibility") PostVisibility publicVisibility,
            @Param("publicAccount") AccountVisibility publicAccount,
            Pageable pageable);

    @Query("""
                SELECT p FROM Post p
                WHERE p.author.id = :authorId
                ORDER BY p.createdAt DESC
            """)
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(
            @Param("authorId") UUID authorId,
            Pageable pageable);

    @Query("""
                SELECT p.id FROM Post p
                WHERE p.author.id = :authorId
            """)
    List<UUID> findActiveIdsByAuthorId(
            @Param("authorId") UUID authorId);

    @Query("""
                SELECT DISTINCT p.author.id FROM Post p
                WHERE p.createdAt >= :createdAfter
            """)
    List<UUID> findDistinctActiveAuthorIdsSince(
            @Param("createdAfter") LocalDateTime createdAfter);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE Post p
                SET p.deletedAt = :deletedAt
                WHERE p.id = :postId
            """)
    void softDeleteById(
            @Param("postId") UUID postId,
            @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE Post p
                SET p.deletedAt = :deletedAt
                WHERE p.author.id = :authorId
                AND p.deletedAt IS NULL
            """)
    void softDeleteByAuthorId(
            @Param("authorId") UUID authorId,
            @Param("deletedAt") LocalDateTime deletedAt);

    @Query("""
                SELECT COUNT(p) FROM Post p
                WHERE p.createdAt >= :createdAfter
            """)
    long countCreatedAfter(
            @Param("createdAfter") LocalDateTime createdAfter);

    @Query(value = """
                SELECT COUNT(*) FROM posts
                WHERE deleted_at IS NOT NULL
                AND deleted_at >= :deletedAfter
            """, nativeQuery = true)
    long countSoftDeletedAfter(
            @Param("deletedAfter") LocalDateTime deletedAfter);

}
