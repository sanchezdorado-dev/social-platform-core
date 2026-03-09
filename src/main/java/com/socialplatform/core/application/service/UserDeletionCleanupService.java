package com.socialplatform.core.application.service;

import java.util.UUID;

public interface UserDeletionCleanupService {

    void cleanupForSoftDeletedUser(UUID userId);
}
