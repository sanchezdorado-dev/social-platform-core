package com.socialplatform.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_role_name", columnList = "name"),
        @Index(name = "idx_role_is_default", columnList = "is_default")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class Role extends BaseEntity {

    @Setter(AccessLevel.PROTECTED)
    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false, length = 50, updatable = false)
    private String name;

    @Setter
    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @Setter
    @Column(name = "is_default", nullable = false)
    private boolean defaultRole;

    public static final String PREFIX = "ROLE_";
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String SUPERADMIN = "SUPER_ADMIN";

    public String getAuthorityName() {
        return PREFIX + this.name;
    }

    public boolean isAdmin() {
        return ADMIN.equalsIgnoreCase(this.name);
    }

    public boolean isSuperAdmin() {
        return SUPERADMIN.equalsIgnoreCase(this.name);
    }
}
