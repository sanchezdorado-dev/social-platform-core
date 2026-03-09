package com.socialplatform.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReasonRequest(
        @NotBlank(message = "Reason is mandatory") @Size(max = 1000, message = "Reason cannot exceed 1000 characters") String reason) {
}
