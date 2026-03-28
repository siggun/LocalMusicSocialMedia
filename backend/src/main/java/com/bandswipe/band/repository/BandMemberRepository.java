package com.bandswipe.band.repository;

import com.bandswipe.band.entity.BandMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BandMemberRepository extends JpaRepository<BandMember, UUID> {

    Optional<BandMember> findByBandIdAndUserId(UUID bandId, UUID userId);

    boolean existsByBandIdAndUserId(UUID bandId, UUID userId);
}
