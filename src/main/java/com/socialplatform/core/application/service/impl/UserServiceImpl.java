package com.socialplatform.core.application.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.socialplatform.core.application.dto.request.PasswordRequest;
import com.socialplatform.core.application.dto.request.UserAdminUpdateRequest;
import com.socialplatform.core.application.dto.response.UserResponse;
import com.socialplatform.core.application.exception.CurrentPasswordMismatchException;
import com.socialplatform.core.application.exception.DuplicateResourceException;
import com.socialplatform.core.application.exception.InsufficientPermissionsException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.mapper.UserMapper;
import com.socialplatform.core.application.service.UserDeletionCleanupService;
import com.socialplatform.core.application.service.UserService;
import com.socialplatform.core.domain.model.User;
import com.socialplatform.core.domain.repository.RoleRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDeletionCleanupService userDeletionCleanupService;

    @Override
    public UserResponse getById(UUID id) {
        return userMapper.toResponse(findEntityById(id));
    }

    @Override
    public UserResponse getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public Page<UserResponse> getAllActive(Pageable pageable) {
        return userRepository.findByDeletedAtIsNull(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public void update(UUID id, UserAdminUpdateRequest request, UUID currentUserId) {
        var targetUser = findEntityById(id);
        var currentRequester = findEntityById(currentUserId);
        boolean isSelfUpdate = id.equals(currentUserId);
        boolean requesterIsSuperAdmin = currentRequester.getRole().isSuperAdmin();
        boolean requesterIsAdmin = currentRequester.getRole().isAdmin();

        if (!requesterIsSuperAdmin && requesterIsAdmin && !isSelfUpdate) {
            if (targetUser.getRole().isAdmin()) {
                throw new InsufficientPermissionsException(
                        "Admins cannot edit other admins.");
            }
        }

        if (userRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new DuplicateResourceException("Username already in use");
        }

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email already in use");
        }

        var role = targetUser.getRole();
        if (!isSelfUpdate && StringUtils.hasText(request.role())) {
            role = roleRepository.findByNameIgnoreCase(request.role())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.role()));
        }

        var status = targetUser.getStatus();
        if (!isSelfUpdate && request.status() != null) {
            status = request.status();
        }

        if (!requesterIsSuperAdmin && !isSelfUpdate) {
            if (role.isSuperAdmin() || role.isAdmin()) {
                throw new InsufficientPermissionsException("Only a Super Admin can assign administrative roles.");
            }
        }

        targetUser.setUsername(request.username());
        targetUser.setEmail(request.email());
        targetUser.setRole(role);
        targetUser.setStatus(status);

        userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public void updateRole(UUID id, String roleName, UUID currentUserId) {
        var targetUser = findEntityById(id);
        var currentRequester = findEntityById(currentUserId);

        if (!currentRequester.getRole().isSuperAdmin() && currentRequester.getRole().isAdmin()) {
            if (targetUser.getRole().isAdmin()) {
                throw new InsufficientPermissionsException(
                        "Admins cannot change the role of other admins.");
            }
        }

        var newRole = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        if (!currentRequester.getRole().isSuperAdmin()) {
            if (newRole.isSuperAdmin() || newRole.isAdmin()) {
                throw new InsufficientPermissionsException("Only a Super Admin can assign administrative roles.");
            }
        }

        targetUser.setRole(newRole);
        userRepository.save(targetUser);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, PasswordRequest request) {
        var user = findEntityById(userId);

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new CurrentPasswordMismatchException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void delete(UUID id, UUID currentUserId) {
        var targetUser = findEntityById(id);
        var currentRequester = findEntityById(currentUserId);

        if (id.equals(currentUserId) && targetUser.getRole().isAdmin()) {
            throw new InsufficientPermissionsException(
                    "Admins cannot delete their own admin account.");
        }

        if (!id.equals(currentUserId)) {
            if (!currentRequester.getRole().isSuperAdmin() && currentRequester.getRole().isAdmin()) {
                if (targetUser.getRole().isAdmin()) {
                    throw new InsufficientPermissionsException(
                            "Admins cannot delete other admins.");
                }
            }
        }

        targetUser.delete();
        userRepository.save(targetUser);
        userDeletionCleanupService.cleanupForSoftDeletedUser(id);
    }

    private User findEntityById(UUID id) {
        return userRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
