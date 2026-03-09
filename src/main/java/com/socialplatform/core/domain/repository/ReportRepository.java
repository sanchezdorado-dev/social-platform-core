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

import com.socialplatform.core.domain.enums.ReportStatus;
import com.socialplatform.core.domain.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

        @Query("SELECT r FROM Report r WHERE r.deletedAt IS NULL")
        Page<Report> findAllActive(Pageable pageable);

        @Query("SELECT r FROM Report r WHERE r.deletedAt IS NULL AND r.status = :status ORDER BY r.createdAt DESC")
        Page<Report> findByStatus(@Param("status") ReportStatus status, Pageable pageable);

        @Query("SELECT r FROM Report r WHERE r.deletedAt IS NULL AND r.id = :id")
        Optional<Report> findActiveById(@Param("id") UUID id);

        @Query("SELECT r FROM Report r WHERE r.deletedAt IS NULL AND r.status = :status")
        List<Report> findAllPendingActive(@Param("status") ReportStatus status);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE Report r SET r.status = :resolvedStatus WHERE r.deletedAt IS NULL AND r.status = :pendingStatus AND r.post.author.id = :authorId")
        void resolveAllPendingByPostAuthorId(@Param("authorId") UUID authorId,
                        @Param("pendingStatus") ReportStatus pendingStatus,
                        @Param("resolvedStatus") ReportStatus resolvedStatus);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE Report r SET r.deletedAt = :deletedAt WHERE r.deletedAt IS NULL AND r.reporter.id = :reporterId")
        void softDeleteAllByReporterId(@Param("reporterId") UUID reporterId,
                        @Param("deletedAt") LocalDateTime deletedAt);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE Report r SET r.status = :resolvedStatus WHERE r.post.id = :postId AND r.status = :pendingStatus")
        void resolveAllPendingReportsForPost(@Param("postId") UUID postId,
                        @Param("pendingStatus") ReportStatus pendingStatus,
                        @Param("resolvedStatus") ReportStatus resolvedStatus);

        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Report r WHERE r.deletedAt IS NULL AND r.reporter.id = :reporterId AND r.post.id = :postId")
        boolean existsByReporterIdAndPostId(@Param("reporterId") UUID reporterId, @Param("postId") UUID postId);

        @Query("SELECT COUNT(r) FROM Report r WHERE r.deletedAt IS NULL AND r.status = :status")
        long countByStatus(@Param("status") ReportStatus status);

        @Query("SELECT COUNT(r) FROM Report r WHERE r.deletedAt IS NULL AND r.createdAt >= :createdAfter")
        long countCreatedAfter(@Param("createdAfter") LocalDateTime createdAfter);

        @Query("SELECT COUNT(r) FROM Report r WHERE r.deletedAt IS NULL AND r.status = :status AND r.updatedAt >= :updatedAfter")
        long countByStatusUpdatedAfter(@Param("status") ReportStatus status,
                        @Param("updatedAfter") LocalDateTime updatedAfter);

        @Query("SELECT COUNT(r) FROM Report r WHERE r.deletedAt IS NULL AND r.createdAt >= :fromTime AND r.createdAt < :toTime")
        long countCreatedBetween(@Param("fromTime") LocalDateTime fromTime, @Param("toTime") LocalDateTime toTime);

        @Query("SELECT COUNT(r) FROM Report r WHERE r.deletedAt IS NULL AND r.status = :status AND r.updatedAt >= :fromTime AND r.updatedAt < :toTime")
        long countByStatusUpdatedBetween(
                        @Param("status") ReportStatus status,
                        @Param("fromTime") LocalDateTime fromTime,
                        @Param("toTime") LocalDateTime toTime);

}
