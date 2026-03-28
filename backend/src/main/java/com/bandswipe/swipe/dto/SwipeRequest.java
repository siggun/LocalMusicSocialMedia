package com.bandswipe.swipe.dto;

import com.bandswipe.shared.enums.SwipeDirection;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SwipeRequest(
        @NotNull UUID targetUserId,
        @NotNull SwipeDirection direction
) {
}
