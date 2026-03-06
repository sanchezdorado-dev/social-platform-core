package com.socialplatform.core.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.enums.UserStatus;
import com.socialplatform.core.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
            SELECT u
            FROM User u
            WHERE UPPER(u.role.name) IN ('ADMIN', 'SUPER_ADMIN')
            AND u.status = :status
            """)
    List<User> findAllActiveAdmins(@Param("status") UserStatus status);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

}
