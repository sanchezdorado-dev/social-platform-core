package com.socialplatform.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReactionType {

    LIKE("Standard like reaction"),
    LOVE("Love reaction"),
    HAHA("Funny reaction"),
    WOW("Surprised reaction"),
    SAD("Sad reaction"),
    ANGRY("Angry reaction"),
    CARE("Care reaction");

    private final String description;
}
