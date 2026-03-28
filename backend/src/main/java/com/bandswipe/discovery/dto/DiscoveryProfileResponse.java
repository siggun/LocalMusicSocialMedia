package com.bandswipe.discovery.dto;

import com.bandswipe.profile.dto.ProfileResponse;
import com.bandswipe.profile.entity.MusicianProfile;
import com.bandswipe.shared.enums.SkillLevel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record DiscoveryProfileResponse(
        UUID id,
        UUID userId,
        String displayName,
        String bio,
        SkillLevel skillLevel,
        String photoUrl,
        String audioIntroUrl,
        Double latitude,
        Double longitude,
        Double distanceMiles,
        List<ProfileResponse.InstrumentDto> instruments,
        List<ProfileResponse.GenreDto> genres,
        String soundcloudUrl,
        String spotifyUrl,
        String youtubeUrl,
        String bandcampUrl,
        double compatibilityScore
) {

    public static DiscoveryProfileResponse fromProfile(MusicianProfile profile,
                                                       double distanceMiles,
                                                       double compatibilityScore) {
        List<ProfileResponse.InstrumentDto> instrumentDtos = profile.getInstruments().stream()
                .map(i -> new ProfileResponse.InstrumentDto(i.getId(), i.getName(), i.getIcon()))
                .collect(Collectors.toList());

        List<ProfileResponse.GenreDto> genreDtos = profile.getGenres().stream()
                .map(g -> new ProfileResponse.GenreDto(g.getId(), g.getName(), g.getIcon()))
                .collect(Collectors.toList());

        return new DiscoveryProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getSkillLevel(),
                profile.getPhotoUrl(),
                profile.getAudioIntroUrl(),
                profile.getLatitude(),
                profile.getLongitude(),
                distanceMiles,
                instrumentDtos,
                genreDtos,
                profile.getSoundcloudUrl(),
                profile.getSpotifyUrl(),
                profile.getYoutubeUrl(),
                profile.getBandcampUrl(),
                compatibilityScore
        );
    }
}
