package com.socialplatform.core.application.service.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.event.AdminAccountCreatedEvent;
import com.socialplatform.core.application.event.AdminPrivilegesRevokedEvent;
import com.socialplatform.core.application.event.AdminSoftDeletedEvent;
import com.socialplatform.core.application.exception.AdminAlreadyDeletedException;
import com.socialplatform.core.application.exception.EmailAlreadyInUseException;
import com.socialplatform.core.application.exception.NotAnAdminException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.exception.SelfDeletionException;
import com.socialplatform.core.application.exception.SelfRevocationException;
import com.socialplatform.core.application.exception.UsernameAlreadyExistsException;
import com.socialplatform.core.application.service.AdminManagementService;
import com.socialplatform.core.application.service.UserDeletionCleanupService;
import com.socialplatform.core.domain.model.Profile;
import com.socialplatform.core.domain.model.Role;
import com.socialplatform.core.domain.model.User;
import com.socialplatform.core.domain.repository.ProfileRepository;
import com.socialplatform.core.domain.repository.RoleRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminManagementServiceImpl implements AdminManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final UserDeletionCleanupService userDeletionCleanupService;

    @Override
    @Transactional
    public UUID createAdminAccount(String username, String email, String password, UUID creatorAdminId,
            String reason) {

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException("Email already exists");
        }

        var adminRole = roleRepository.findByNameIgnoreCase(Role.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("ADMIN role not found"));

        var newAdmin = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(adminRole)
                .build();

        var savedAdmin = userRepository.save(newAdmin);

        var profile = Profile.builder()
                .user(savedAdmin)
                .firstName("Admin")
                .lastName(username)
                .build();
        profileRepository.save(profile);

        eventPublisher.publishEvent(new AdminAccountCreatedEvent(
                savedAdmin.getId(),
                savedAdmin.getUsername(),
                creatorAdminId,
                reason));

        return savedAdmin.getId();
    }

    @Override
    @Transactional
    public void revokeAdminPrivileges(UUID adminId, UUID executorAdminId, String reason) {
        var admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        var roleName = admin.getRole().getName();
        if (!Role.ADMIN.equalsIgnoreCase(roleName)) {
            throw new NotAnAdminException("User is not an admin");
        }

        if (adminId.equals(executorAdminId)) {
            throw new SelfRevocationException("Cannot revoke your own admin privileges");
        }

        var userRole = roleRepository.findByNameIgnoreCase(Role.USER)
                .orElseThrow(() -> new ResourceNotFoundException("USER role not found"));

        admin.setRole(userRole);
        userRepository.save(admin);

        eventPublisher.publishEvent(new AdminPrivilegesRevokedEvent(
                adminId,
                admin.getUsername(),
                executorAdminId,
                reason));
    }

    @Override
    @Transactional
    public void softDeleteAdmin(UUID adminId, UUID executorAdminId, String reason) {
        var admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        var roleName = admin.getRole().getName();
        if (!Role.ADMIN.equalsIgnoreCase(roleName)) {
            throw new NotAnAdminException("User is not an admin");
        }

        if (adminId.equals(executorAdminId)) {
            throw new SelfDeletionException("Cannot delete your own admin account");
        }

        if (admin.isDeleted()) {
            throw new AdminAlreadyDeletedException("Admin is already deleted");
        }

        admin.delete();
        userRepository.save(admin);
        userDeletionCleanupService.cleanupForSoftDeletedUser(adminId);

        eventPublisher.publishEvent(new AdminSoftDeletedEvent(
                adminId,
                admin.getUsername(),
                executorAdminId,
                reason));
    }
}
