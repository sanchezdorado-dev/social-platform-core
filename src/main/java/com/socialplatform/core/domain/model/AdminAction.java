package com.socialplatform.core.domain.model;

import java.util.UUID;

import com.socialplatform.core.domain.enums.AdminActionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admin_actions", indexes = {
        @Index(name = "idx_admin_actions_admin_id", columnList = "admin_id"),
        @Index(name = "idx_admin_actions_type_date", columnList = "admin_action_type, created_at"),
        @Index(name = "idx_admin_actions_affected_user_id", columnList = "affected_user_id"),
        @Index(name = "idx_admin_actions_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class AdminAction extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_action_type", nullable = false, length = 50, updatable = false)
    private AdminActionType adminActionType;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500, updatable = false)
    private String description;

    @Size(max = 1000)
    @Column(length = 1000, updatable = false)
    private String reason;

    @NotNull
    @Column(name = "object_id", nullable = false, updatable = false)
    private UUID objectId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "object_table", nullable = false, length = 100, updatable = false)
    private String objectTable;

    @NotNull
    @Column(name = "admin_id", nullable = false, updatable = false)
    private UUID adminId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "admin_username", nullable = false, length = 50, updatable = false)
    private String adminUsername;

    @NotBlank
    @Size(max = 20)
    @Column(name = "admin_role", nullable = false, length = 20, updatable = false)
    private String adminRole;

    @Column(name = "affected_user_id", updatable = false)
    private UUID affectedUserId;

    @Size(max = 50)
    @Column(name = "affected_username", length = 50, updatable = false)
    private String affectedUsername;
}
