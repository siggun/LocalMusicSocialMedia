package com.bandswipe.profile.dto;

import com.bandswipe.shared.enums.Availability;
import com.bandswipe.shared.enums.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateProfileRequest(
        @NotBlank String displayName,
        @Size(max = 500) String bio,
        @NotNull SkillLevel skillLevel,
        Set<Availability> availability,
        @NotEmpty Set<Integer> instrumentIds,
        @NotEmpty Set<Integer> genreIds,
        Double latitude,
        Double longitude,
        Integer maxDistanceMiles,
        String soundcloudUrl,
        String spotifyUrl,
        String youtubeUrl,
        String bandcampUrl
) {
}
