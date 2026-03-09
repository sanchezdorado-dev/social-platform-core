package com.socialplatform.core.application.service.impl;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.request.AdminActionRequest;
import com.socialplatform.core.application.dto.response.AdminActionResponse;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.mapper.AdminActionMapper;
import com.socialplatform.core.application.service.AdminActionService;
import com.socialplatform.core.application.service.AdminRoleResolver;
import com.socialplatform.core.domain.enums.AdminActionType;
import com.socialplatform.core.domain.model.AdminAction;
import com.socialplatform.core.domain.repository.AdminActionRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminActionServiceImpl implements AdminActionService {

    private final AdminActionRepository adminActionRepository;
    private final UserRepository userRepository;
    private final AdminActionMapper adminActionMapper;
    private final AdminRoleResolver adminRoleResolver;

    @Override
    @Transactional
    public AdminActionResponse executeAndLog(AdminActionRequest request, UUID adminId, String adminUsername) {

        var admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + adminId));

        String normalizedRole = normalizeRoleName(admin.getRole().getName());
        if (!isAdminRole(normalizedRole)) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }

        var action = AdminAction.builder()
                .adminActionType(request.adminActionType())
                .description(request.description())
                .reason(request.reason())
                .objectId(request.objectId())
                .objectTable(request.objectTable())
                .affectedUserId(request.affectedUserId())
                .affectedUsername(request.affectedUsername())
                .adminId(adminId)
                .adminUsername(adminUsername)
                .adminRole(normalizedRole)
                .build();

        var savedAction = adminActionRepository.save(action);

        return adminActionMapper.toResponse(savedAction);
    }

    @Override
    public Page<AdminActionResponse> getAuditLog(Pageable pageable) {
        Set<String> visibleRoles = adminRoleResolver.resolveVisibleRoles();
        return adminActionRepository.findByAdminRoleIn(visibleRoles, pageable)
                .map(adminActionMapper::toResponse);
    }

    @Override
    public Page<AdminActionResponse> getLogByAdmin(UUID adminId, Pageable pageable) {
        if (!userRepository.existsById(adminId)) {
            throw new ResourceNotFoundException("Admin user not found");
        }

        Set<String> visibleRoles = adminRoleResolver.resolveVisibleRoles();

        return adminActionRepository.findByAdminIdAndAdminRoleIn(adminId, visibleRoles, pageable)
                .map(adminActionMapper::toResponse);
    }

    @Override
    public Page<AdminActionResponse> getLogByType(AdminActionType actionType, Pageable pageable) {
        Set<String> visibleRoles = adminRoleResolver.resolveVisibleRoles();

        return adminActionRepository.findByAdminActionTypeAndAdminRoleIn(actionType, visibleRoles, pageable)
                .map(adminActionMapper::toResponse);
    }

    @Override
    public AdminActionResponse getById(UUID id) {
        Set<String> visibleRoles = adminRoleResolver.resolveVisibleRoles();

        var action = adminActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit record not found"));

        if (!visibleRoles.contains(action.getAdminRole())) {
            throw new ResourceNotFoundException("Audit record not found");
        }

        return adminActionMapper.toResponse(action);
    }

    private boolean isAdminRole(String normalizedRole) {
        return "ADMIN".equals(normalizedRole) || "SUPERADMIN".equals(normalizedRole);
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null) {
            return "ADMIN";
        }

        String normalized = roleName.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            return normalized.substring("ROLE_".length());
        }
        return normalized;
    }
}
