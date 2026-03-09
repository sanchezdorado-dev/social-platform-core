package com.socialplatform.core.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.request.PasswordRequest;
import com.socialplatform.core.application.dto.request.UserAdminUpdateRequest;
import com.socialplatform.core.application.dto.response.UserResponse;

public interface UserService {

    Page<UserResponse> getAllActive(Pageable pageable);

    UserResponse getById(UUID id);

    UserResponse getByUsername(String username);

    void update(UUID id, UserAdminUpdateRequest request, UUID currentUserId);

    void updateRole(UUID id, String roleName, UUID currentUserId);

    void changePassword(UUID userId, PasswordRequest request);

    void delete(UUID id, UUID currentUserId);
}
