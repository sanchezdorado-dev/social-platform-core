package com.socialplatform.core.application.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.socialplatform.core.domain.model.Role;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminRoleResolver {

    public Set<String> resolveVisibleRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = new HashSet<>();

        if (auth == null) {
            return roles;
        }

        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.PREFIX + Role.SUPERADMIN));

        if (isSuperAdmin) {
            roles.add(Role.ADMIN);
            roles.add(Role.SUPERADMIN);
            return roles;
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.PREFIX + Role.ADMIN));

        if (isAdmin) {
            roles.add(Role.ADMIN);
        }

        return roles;
    }
}
