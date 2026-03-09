package com.socialplatform.core.application.event;

import java.util.UUID;

public record NewFollowerEvent(
        UUID followerId,
        UUID followedId) {
}
