package com.bandswipe.profile.repository;

import com.bandswipe.profile.entity.MusicianProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<MusicianProfile, UUID> {

    Optional<MusicianProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query(value = """
            SELECT p.* FROM musician_profiles p
            WHERE p.user_id != :userId
            AND p.user_id NOT IN (
                SELECT sh.swiped_id FROM swipe_history sh WHERE sh.swiper_id = :userId
            )
            AND ST_DWithin(
                p.location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                :distanceMeters
            )
            ORDER BY ST_Distance(
                p.location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
            )
            """, nativeQuery = true)
    Page<MusicianProfile> findNearbyProfiles(
            @Param("userId") UUID userId,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceMeters") double distanceMeters,
            Pageable pageable
    );
}
