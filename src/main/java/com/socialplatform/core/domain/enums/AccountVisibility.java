package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountVisibility {

    PUBLIC("Public content visible to everyone"),
    PRIVATE("Private content visible only to the owner");

    private final String description;
}
