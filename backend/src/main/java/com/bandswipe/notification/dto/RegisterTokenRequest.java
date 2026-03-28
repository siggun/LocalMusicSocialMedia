package com.bandswipe.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterTokenRequest(
        @NotBlank(message = "Token must not be blank")
        String token,

        String deviceType
) {
    public RegisterTokenRequest {
        if (deviceType == null || deviceType.isBlank()) {
            deviceType = "MOBILE";
        }
    }
}
