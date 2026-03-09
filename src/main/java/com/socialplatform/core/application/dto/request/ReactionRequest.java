package com.socialplatform.core.application.dto.request;

import com.socialplatform.core.domain.enums.ReactionType;

import jakarta.validation.constraints.NotNull;

public record ReactionRequest(
        @NotNull(message = "Reaction type must be specified") ReactionType type) {
}
