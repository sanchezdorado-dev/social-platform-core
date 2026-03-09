package com.socialplatform.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "Comment content is mandatory") @Size(max = 1000, message = "Comment cannot exceed 1000 characters") String content) {
}
