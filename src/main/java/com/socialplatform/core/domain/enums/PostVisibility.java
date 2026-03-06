package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostVisibility {

    PUBLIC("Visible to everyone or approved followers depending on account visibility"),
    ONLY_ME("Visible only to the author regardless of account visibility");

    private final String description;
}
