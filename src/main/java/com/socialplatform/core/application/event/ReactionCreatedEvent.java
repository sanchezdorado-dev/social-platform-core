package com.socialplatform.core.application.event;

import java.util.UUID;

public record ReactionCreatedEvent(
        UUID reactionId,
        UUID postId,
        UUID userId,
        UUID postAuthorId,
        String reactionType) {
}
