package com.bandswipe.profile.service;

import com.bandswipe.auth.entity.User;
import com.bandswipe.auth.repository.UserRepository;
import com.bandswipe.profile.dto.CreateProfileRequest;
import com.bandswipe.profile.dto.ProfileResponse;
import com.bandswipe.profile.dto.UpdateLocationRequest;
import com.bandswipe.profile.dto.UpdateProfileRequest;
import com.bandswipe.profile.entity.MusicianProfile;
import com.bandswipe.profile.repository.GenreRepository;
import com.bandswipe.profile.repository.InstrumentRepository;
import com.bandswipe.profile.repository.ProfileRepository;
import com.bandswipe.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final InstrumentRepository instrumentRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public ProfileResponse createProfile(UUID userId, CreateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Profile already exists for this user");
        }

        MusicianProfile profile = MusicianProfile.builder()
                .user(user)
                .displayName(req.displayName())
                .bio(req.bio())
                .skillLevel(req.skillLevel())
                .availability(req.availability())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .maxDistanceMiles(req.maxDistanceMiles() != null ? req.maxDistanceMiles() : 25)
                .soundcloudUrl(req.soundcloudUrl())
                .spotifyUrl(req.spotifyUrl())
                .youtubeUrl(req.youtubeUrl())
                .bandcampUrl(req.bandcampUrl())
                .instruments(new HashSet<>(instrumentRepository.findByIdIn(req.instrumentIds())))
                .genres(new HashSet<>(genreRepository.findByIdIn(req.genreIds())))
                .build();

        if (req.latitude() != null && req.longitude() != null) {
            profile.setLocation(createPoint(req.longitude(), req.latitude()));
        }

        MusicianProfile saved = profileRepository.save(profile);
        return ProfileResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(UUID userId) {
        MusicianProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", userId));
        return ProfileResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByProfileId(UUID profileId) {
        MusicianProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", profileId));
        return ProfileResponse.fromEntity(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest req) {
        MusicianProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", userId));

        if (req.displayName() != null) {
            profile.setDisplayName(req.displayName());
        }
        if (req.bio() != null) {
            profile.setBio(req.bio());
        }
        if (req.skillLevel() != null) {
            profile.setSkillLevel(req.skillLevel());
        }
        if (req.availability() != null) {
            profile.setAvailability(req.availability());
        }
        if (req.instrumentIds() != null) {
            profile.setInstruments(new HashSet<>(instrumentRepository.findByIdIn(req.instrumentIds())));
        }
        if (req.genreIds() != null) {
            profile.setGenres(new HashSet<>(genreRepository.findByIdIn(req.genreIds())));
        }
        if (req.latitude() != null && req.longitude() != null) {
            profile.setLatitude(req.latitude());
            profile.setLongitude(req.longitude());
            profile.setLocation(createPoint(req.longitude(), req.latitude()));
        }
        if (req.maxDistanceMiles() != null) {
            profile.setMaxDistanceMiles(req.maxDistanceMiles());
        }
        if (req.soundcloudUrl() != null) {
            profile.setSoundcloudUrl(req.soundcloudUrl());
        }
        if (req.spotifyUrl() != null) {
            profile.setSpotifyUrl(req.spotifyUrl());
        }
        if (req.youtubeUrl() != null) {
            profile.setYoutubeUrl(req.youtubeUrl());
        }
        if (req.bandcampUrl() != null) {
            profile.setBandcampUrl(req.bandcampUrl());
        }

        MusicianProfile saved = profileRepository.save(profile);
        return ProfileResponse.fromEntity(saved);
    }

    @Transactional
    public ProfileResponse updateLocation(UUID userId, UpdateLocationRequest req) {
        MusicianProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", userId));

        profile.setLatitude(req.latitude());
        profile.setLongitude(req.longitude());
        profile.setLocation(createPoint(req.longitude(), req.latitude()));

        MusicianProfile saved = profileRepository.save(profile);
        return ProfileResponse.fromEntity(saved);
    }

    @Transactional
    public ProfileResponse updatePhotoUrl(UUID userId, String url) {
        MusicianProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", userId));

        profile.setPhotoUrl(url);

        MusicianProfile saved = profileRepository.save(profile);
        return ProfileResponse.fromEntity(saved);
    }

    @Transactional
    public ProfileResponse updateAudioUrl(UUID userId, String url) {
        MusicianProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", userId));

        profile.setAudioIntroUrl(url);

        MusicianProfile saved = profileRepository.save(profile);
        return ProfileResponse.fromEntity(saved);
    }

    private Point createPoint(double longitude, double latitude) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }
}
