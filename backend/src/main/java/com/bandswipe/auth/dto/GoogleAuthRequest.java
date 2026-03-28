package com.bandswipe.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        @NotBlank String firebaseToken,
        @NotBlank String email,
        String displayName
) {
}
