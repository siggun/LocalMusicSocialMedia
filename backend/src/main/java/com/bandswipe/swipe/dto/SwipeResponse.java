package com.bandswipe.swipe.dto;

import java.util.UUID;

public record SwipeResponse(
        boolean matched,
        UUID matchId
) {
}
