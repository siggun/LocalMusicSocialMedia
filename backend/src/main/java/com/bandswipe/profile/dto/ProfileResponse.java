package com.bandswipe.profile.dto;

import com.bandswipe.profile.entity.MusicianProfile;
import com.bandswipe.shared.enums.Availability;
import com.bandswipe.shared.enums.SkillLevel;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProfileResponse(
        UUID id,
        UUID userId,
        String displayName,
        String bio,
        SkillLevel skillLevel,
        Set<Availability> availability,
        String photoUrl,
        String audioIntroUrl,
        Double latitude,
        Double longitude,
        Integer maxDistanceMiles,
        Set<InstrumentDto> instruments,
        Set<GenreDto> genres,
        String soundcloudUrl,
        String spotifyUrl,
        String youtubeUrl,
        String bandcampUrl,
        LocalDateTime createdAt
) {

    public record InstrumentDto(Integer id, String name, String icon) {
    }

    public record GenreDto(Integer id, String name, String icon) {
    }

    public static ProfileResponse fromEntity(MusicianProfile profile) {
        Set<InstrumentDto> instrumentDtos = profile.getInstruments().stream()
                .map(i -> new InstrumentDto(i.getId(), i.getName(), i.getIcon()))
                .collect(Collectors.toSet());

        Set<GenreDto> genreDtos = profile.getGenres().stream()
                .map(g -> new GenreDto(g.getId(), g.getName(), g.getIcon()))
                .collect(Collectors.toSet());

        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getSkillLevel(),
                profile.getAvailability(),
                profile.getPhotoUrl(),
                profile.getAudioIntroUrl(),
                profile.getLatitude(),
                profile.getLongitude(),
                profile.getMaxDistanceMiles(),
                instrumentDtos,
                genreDtos,
                profile.getSoundcloudUrl(),
                profile.getSpotifyUrl(),
                profile.getYoutubeUrl(),
                profile.getBandcampUrl(),
                profile.getCreatedAt()
        );
    }
}
