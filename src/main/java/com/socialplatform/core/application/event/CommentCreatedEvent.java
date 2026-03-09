package com.socialplatform.core.application.event;

import java.util.UUID;

public record CommentCreatedEvent(
        UUID commentId,
        UUID postId,
        UUID authorId,
        UUID postAuthorId) {
}
