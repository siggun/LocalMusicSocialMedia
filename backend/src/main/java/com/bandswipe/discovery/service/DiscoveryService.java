package com.bandswipe.discovery.service;

import com.bandswipe.discovery.dto.DiscoveryProfileResponse;
import com.bandswipe.profile.entity.MusicianProfile;
import com.bandswipe.profile.repository.ProfileRepository;
import com.bandswipe.shared.enums.SkillLevel;
import com.bandswipe.shared.exception.ResourceNotFoundException;
import com.bandswipe.swipe.repository.SwipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscoveryService {

    private static final double MILES_TO_METERS = 1609.34;
    private static final double EARTH_RADIUS_MILES = 3958.8;

    private final ProfileRepository profileRepository;
    private final SwipeRepository swipeRepository;

    @Transactional(readOnly = true)
    public Page<DiscoveryProfileResponse> getFeed(UUID userId,
                                                   Integer maxDistance,
                                                   Set<Integer> genreIds,
                                                   Set<Integer> instrumentIds,
                                                   SkillLevel skillLevel,
                                                   Pageable pageable) {

        MusicianProfile currentProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));

        double distanceMeters = maxDistance * MILES_TO_METERS;

        // Fetch a larger page to allow for in-memory filtering before re-paging
        Page<MusicianProfile> nearbyProfiles = profileRepository.findNearbyProfiles(
                userId,
                currentProfile.getLatitude(),
                currentProfile.getLongitude(),
                distanceMeters,
                Pageable.unpaged()
        );

        List<DiscoveryProfileResponse> results = nearbyProfiles.getContent().stream()
                .filter(profile -> matchesFilters(profile, genreIds, instrumentIds, skillLevel))
                .map(profile -> {
                    double distMiles = haversineDistance(
                            currentProfile.getLatitude(), currentProfile.getLongitude(),
                            profile.getLatitude(), profile.getLongitude()
                    );
                    double score = calculateCompatibilityScore(currentProfile, profile, distMiles, maxDistance);
                    return DiscoveryProfileResponse.fromProfile(profile, distMiles, score);
                })
                .sorted(Comparator.comparingDouble(DiscoveryProfileResponse::compatibilityScore).reversed())
                .toList();

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), results.size());

        if (start >= results.size()) {
            return new PageImpl<>(List.of(), pageable, results.size());
        }

        List<DiscoveryProfileResponse> pageContent = results.subList(start, end);
        return new PageImpl<>(pageContent, pageable, results.size());
    }

    private boolean matchesFilters(MusicianProfile profile,
                                   Set<Integer> genreIds,
                                   Set<Integer> instrumentIds,
                                   SkillLevel skillLevel) {
        if (genreIds != null && !genreIds.isEmpty()) {
            boolean hasMatchingGenre = profile.getGenres().stream()
                    .anyMatch(g -> genreIds.contains(g.getId()));
            if (!hasMatchingGenre) {
                return false;
            }
        }

        if (instrumentIds != null && !instrumentIds.isEmpty()) {
            boolean hasMatchingInstrument = profile.getInstruments().stream()
                    .anyMatch(i -> instrumentIds.contains(i.getId()));
            if (!hasMatchingInstrument) {
                return false;
            }
        }

        if (skillLevel != null && profile.getSkillLevel() != skillLevel) {
            return false;
        }

        return true;
    }

    private double calculateCompatibilityScore(MusicianProfile current,
                                               MusicianProfile candidate,
                                               double distanceMiles,
                                               int maxDistance) {
        double score = 0.0;

        // Genre overlap: (shared / total unique) * 40
        Set<Integer> currentGenreIds = new HashSet<>();
        current.getGenres().forEach(g -> currentGenreIds.add(g.getId()));
        Set<Integer> candidateGenreIds = new HashSet<>();
        candidate.getGenres().forEach(g -> candidateGenreIds.add(g.getId()));

        Set<Integer> allGenres = new HashSet<>(currentGenreIds);
        allGenres.addAll(candidateGenreIds);

        if (!allGenres.isEmpty()) {
            Set<Integer> sharedGenres = new HashSet<>(currentGenreIds);
            sharedGenres.retainAll(candidateGenreIds);
            score += ((double) sharedGenres.size() / allGenres.size()) * 40.0;
        }

        // Instrument complementarity: (different / total) * 30
        Set<Integer> currentInstrumentIds = new HashSet<>();
        current.getInstruments().forEach(i -> currentInstrumentIds.add(i.getId()));
        Set<Integer> candidateInstrumentIds = new HashSet<>();
        candidate.getInstruments().forEach(i -> candidateInstrumentIds.add(i.getId()));

        Set<Integer> allInstruments = new HashSet<>(currentInstrumentIds);
        allInstruments.addAll(candidateInstrumentIds);

        if (!allInstruments.isEmpty()) {
            Set<Integer> sharedInstruments = new HashSet<>(currentInstrumentIds);
            sharedInstruments.retainAll(candidateInstrumentIds);
            int differentInstruments = allInstruments.size() - sharedInstruments.size();
            score += ((double) differentInstruments / allInstruments.size()) * 30.0;
        }

        // Skill level proximity: (3 - |ordinal diff|) / 3 * 20
        if (current.getSkillLevel() != null && candidate.getSkillLevel() != null) {
            int ordinalDiff = Math.abs(current.getSkillLevel().ordinal() - candidate.getSkillLevel().ordinal());
            score += ((3.0 - ordinalDiff) / 3.0) * 20.0;
        }

        // Distance bonus: (1 - distance/maxDistance) * 10
        if (maxDistance > 0) {
            double distanceRatio = Math.min(distanceMiles / maxDistance, 1.0);
            score += (1.0 - distanceRatio) * 10.0;
        }

        return Math.round(score * 100.0) / 100.0;
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_MILES * c;
    }
}
