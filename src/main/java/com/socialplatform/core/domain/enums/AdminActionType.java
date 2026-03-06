package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminActionType {

    ACTIVATE_USER("Enable a user account"),
    DEACTIVATE_USER("Disable a user account"),
    SOFT_DELETE_USER("Perform a soft delete on a user"),
    UPDATE_ACCOUNT_CREDENTIALS("Change user credentials"),
    UPDATE_USER_ROLE("Modify user permissions/role"),
    RESOLVE_REPORT("Mark a user report as resolved"),
    REJECT_REPORT("Dismiss a user report"),
    DELETE_CONTENT("Remove user-generated content"),
    CREATE_ADMIN_ACCOUNT("Provision a new administrator"),
    REVOKE_ADMIN_PRIVILEGES("Remove administrative access"),
    SOFT_DELETE_ADMIN("Remove an administrator record");

    private final String description;
}
