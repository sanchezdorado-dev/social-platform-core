package com.socialplatform.core.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByDefaultRoleTrue();

    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findByDeletedAtIsNull();

    boolean existsByNameIgnoreCase(String name);

}
