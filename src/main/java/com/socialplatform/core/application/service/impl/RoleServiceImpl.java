package com.socialplatform.core.application.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.request.RoleRequest;
import com.socialplatform.core.application.dto.response.RoleResponse;
import com.socialplatform.core.application.exception.DuplicateResourceException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.mapper.RoleMapper;
import com.socialplatform.core.application.service.RoleService;
import com.socialplatform.core.domain.model.Role;
import com.socialplatform.core.domain.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse getById(UUID id) {
        return roleRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
    }

    @Override
    public RoleResponse getByName(String name) {
        return roleRepository.findByNameIgnoreCase(name)
                .map(roleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    @Override
    public RoleResponse getDefaultRole() {
        return roleRepository.findByDefaultRoleTrue()
                .map(roleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not configured in the system"));
    }

    @Override
    public List<RoleResponse> getAllActive() {
        return roleRepository.findByDeletedAtIsNull().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Role already exists with name: " + request.name());
        }

        Role role = Role.builder()
                .name(request.name().toUpperCase())
                .description(request.description())
                .defaultRole(Boolean.TRUE.equals(request.isDefault()))
                .build();

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponse update(UUID id, RoleRequest request) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        roleRepository.findByNameIgnoreCase(request.name())
                .ifPresent(existingRole -> {
                    if (!existingRole.getId().equals(id)) {
                        throw new DuplicateResourceException(
                                "Another role already uses the name: " + request.name());
                    }
                });

        role.setDescription(request.description());
        role.setDefaultRole(request.isDefault());

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        role.delete();
        roleRepository.save(role);
    }

}
