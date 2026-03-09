package com.socialplatform.core.application.service;

import java.util.UUID;

public interface UserManagementService {

    void activateUser(UUID userId, UUID adminId, String reason);

    void deactivateUser(UUID userId, UUID adminId, String reason);

    void softDeleteUser(UUID userId, UUID adminId, String reason);

    void updateUserCredentials(UUID userId, String newUsername, String newEmail, String newPassword, UUID adminId,
            String reason);

    void updateUserRole(UUID userId, UUID newRoleId, UUID adminId, String reason);
}
