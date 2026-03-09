package com.socialplatform.core.domain.repository;

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

import com.socialplatform.core.domain.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt WHERE c.post.id = :postId AND c.deletedAt IS NULL")
    void softDeleteByPostId(@Param("postId") UUID postId, @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Comment c SET c.deletedAt = :deletedAt WHERE c.author.id = :authorId AND c.deletedAt IS NULL")
    void softDeleteByAuthorId(@Param("authorId") UUID authorId, @Param("deletedAt") LocalDateTime deletedAt);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<Comment> findByPostId(@Param("postId") UUID postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.author.id = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findActiveById(@Param("id") UUID id);

    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.author.id = :authorId")
    Optional<Comment> findByIdAndAuthorId(@Param("commentId") UUID commentId, @Param("authorId") UUID authorId);

    @Query("SELECT DISTINCT c.author.id FROM Comment c WHERE c.createdAt >= :createdAfter")
    List<UUID> findDistinctAuthorIdsSince(@Param("createdAfter") LocalDateTime createdAfter);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countByPostId(@Param("postId") UUID postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.author.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

}
