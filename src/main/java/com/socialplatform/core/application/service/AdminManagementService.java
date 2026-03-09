package com.socialplatform.core.application.service;

import java.util.UUID;

public interface AdminManagementService {

    UUID createAdminAccount(String username, String email, String password, UUID creatorAdminId, String reason);

    void revokeAdminPrivileges(UUID adminId, UUID executorAdminId, String reason);

    void softDeleteAdmin(UUID adminId, UUID executorAdminId, String reason);
}
