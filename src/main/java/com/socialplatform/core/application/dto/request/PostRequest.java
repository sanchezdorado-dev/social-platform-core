package com.socialplatform.core.application.dto.request;

import com.socialplatform.core.domain.enums.PostVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequest(
        @NotBlank(message = "Content is mandatory") @Size(max = 1500, message = "Content cannot exceed 1500 characters") String content,

        @Size(max = 500, message = "Image URL is too long") String imageUrl,

        @NotNull(message = "Visibility type is mandatory") PostVisibility postVisibility) {
}
