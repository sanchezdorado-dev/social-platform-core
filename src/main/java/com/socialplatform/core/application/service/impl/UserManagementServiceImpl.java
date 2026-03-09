package com.socialplatform.core.application.service.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.event.UserActivatedEvent;
import com.socialplatform.core.application.event.UserCredentialsUpdatedEvent;
import com.socialplatform.core.application.event.UserDeactivatedEvent;
import com.socialplatform.core.application.event.UserRoleUpdatedEvent;
import com.socialplatform.core.application.event.UserSoftDeletedEvent;
import com.socialplatform.core.application.exception.EmailAlreadyInUseException;
import com.socialplatform.core.application.exception.NoChangesProvidedException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.exception.RoleUnchangedException;
import com.socialplatform.core.application.exception.UserAlreadyActiveException;
import com.socialplatform.core.application.exception.UserAlreadyDeactivatedException;
import com.socialplatform.core.application.exception.UserAlreadyDeletedException;
import com.socialplatform.core.application.exception.UsernameAlreadyExistsException;
import com.socialplatform.core.application.service.UserDeletionCleanupService;
import com.socialplatform.core.application.service.UserManagementService;
import com.socialplatform.core.domain.enums.UserStatus;
import com.socialplatform.core.domain.repository.RoleRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final UserDeletionCleanupService userDeletionCleanupService;

    @Override
    @Transactional
    public void activateUser(UUID userId, UUID adminId, String reason) {
        var user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserAlreadyActiveException("User is already active");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserActivatedEvent(
                userId,
                user.getUsername(),
                adminId,
                reason));
    }

    @Override
    @Transactional
    public void deactivateUser(UUID userId, UUID adminId, String reason) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UserAlreadyDeactivatedException("User is already deactivated");
        }

        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserDeactivatedEvent(
                userId,
                user.getUsername(),
                adminId,
                reason));
    }

    @Override
    @Transactional
    public void softDeleteUser(UUID userId, UUID adminId, String reason) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isDeleted()) {
            throw new UserAlreadyDeletedException("User is already deleted");
        }

        user.delete();
        userRepository.save(user);
        userDeletionCleanupService.cleanupForSoftDeletedUser(userId);

        eventPublisher.publishEvent(new UserSoftDeletedEvent(
                userId,
                user.getUsername(),
                adminId,
                reason));
    }

    @Override
    @Transactional
    public void updateUserCredentials(UUID userId, String newUsername, String newEmail, String newPassword,
            UUID adminId, String reason) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var changedField = "";

        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            // Validar que el username no esté en uso
            if (userRepository.existsByUsername(newUsername)) {
                throw new UsernameAlreadyExistsException("Username already in use");
            }
            user.setUsername(newUsername);
            changedField = "username";
        }

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            // Validar que el email no esté en uso
            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyInUseException("Email already in use");
            }
            user.setEmail(newEmail);
            changedField = changedField.isEmpty() ? "email" : changedField + ", email";
        }

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            changedField = changedField.isEmpty() ? "password" : changedField + ", password";
        }

        if (changedField.isEmpty()) {
            throw new NoChangesProvidedException("No changes provided");
        }

        userRepository.save(user);

        eventPublisher.publishEvent(new UserCredentialsUpdatedEvent(
                userId,
                user.getUsername(),
                adminId,
                reason,
                changedField));
    }

    @Override
    @Transactional
    public void updateUserRole(UUID userId, UUID newRoleId, UUID adminId, String reason) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        var oldRoleName = user.getRole().getName();
        var newRoleName = newRole.getName();

        if (oldRoleName.equals(newRoleName)) {
            throw new RoleUnchangedException("User already has this role");
        }

        user.setRole(newRole);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserRoleUpdatedEvent(
                userId,
                user.getUsername(),
                adminId,
                oldRoleName,
                newRoleName,
                reason));
    }
}
