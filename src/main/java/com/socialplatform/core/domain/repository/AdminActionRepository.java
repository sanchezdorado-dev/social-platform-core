package com.socialplatform.core.domain.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.enums.AdminActionType;
import com.socialplatform.core.domain.model.AdminAction;

@Repository
public interface AdminActionRepository extends JpaRepository<AdminAction, UUID> {

    @Query("""
            SELECT a.adminActionType AS adminActionType, COUNT(a) AS count
            FROM AdminAction a
            WHERE a.adminRole IN :adminRoles
              AND a.createdAt >= :createdAfter
            GROUP BY a.adminActionType
            ORDER BY COUNT(a) DESC
            """)
    List<AdminActionTypeCountProjection> countByTypeForRolesSince(
            @Param("adminRoles") Collection<String> adminRoles,
            @Param("createdAfter") LocalDateTime createdAfter);

    Page<AdminAction> findByAdminIdAndAdminRoleIn(UUID adminId, Collection<String> adminRoles, Pageable pageable);

    Page<AdminAction> findByAdminActionTypeAndAdminRoleIn(
            AdminActionType actionType,
            Collection<String> adminRoles,
            Pageable pageable);

    Page<AdminAction> findByAdminRoleIn(Collection<String> adminRoles, Pageable pageable);

    long countByAdminRoleInAndCreatedAtGreaterThanEqual(Collection<String> adminRoles, LocalDateTime createdAt);

}
