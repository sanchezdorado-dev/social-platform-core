package com.socialplatform.core.application.service;

import java.util.List;
import java.util.UUID;

import com.socialplatform.core.application.dto.request.RoleRequest;
import com.socialplatform.core.application.dto.response.RoleResponse;

public interface RoleService {

    List<RoleResponse> getAllActive();

    RoleResponse getById(UUID id);

    RoleResponse getByName(String name);

    RoleResponse getDefaultRole();

    RoleResponse create(RoleRequest request);

    RoleResponse update(UUID id, RoleRequest request);

    void delete(UUID id);
}
