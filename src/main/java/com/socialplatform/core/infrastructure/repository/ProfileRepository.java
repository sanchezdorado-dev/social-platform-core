package com.socialplatform.core.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.model.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Page<Profile> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);

    Optional<Profile> findByUserId(UUID userId);

    Optional<Profile> findByUserUsernameIgnoreCase(String username);

}
