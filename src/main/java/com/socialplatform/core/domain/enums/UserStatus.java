package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    ACTIVE("Account is active and functional"),
    BANNED("Account has been banned due to policy violation");

    private final String description;
}
