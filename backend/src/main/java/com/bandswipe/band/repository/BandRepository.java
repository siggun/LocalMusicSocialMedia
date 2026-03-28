package com.bandswipe.band.repository;

import com.bandswipe.band.entity.Band;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BandRepository extends JpaRepository<Band, UUID> {

    @Query("SELECT DISTINCT b FROM Band b JOIN b.members m WHERE m.user.id = :userId")
    List<Band> findBandsByUserId(UUID userId);
}
