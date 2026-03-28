package com.bandswipe.profile.dto;

import com.bandswipe.shared.enums.Availability;
import com.bandswipe.shared.enums.SkillLevel;

import java.util.Set;

public record UpdateProfileRequest(
        String displayName,
        String bio,
        SkillLevel skillLevel,
        Set<Availability> availability,
        Set<Integer> instrumentIds,
        Set<Integer> genreIds,
        Double latitude,
        Double longitude,
        Integer maxDistanceMiles,
        String soundcloudUrl,
        String spotifyUrl,
        String youtubeUrl,
        String bandcampUrl
) {
}
