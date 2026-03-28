package com.bandswipe.jam.repository;

import com.bandswipe.jam.entity.JamEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JamEventRepository extends JpaRepository<JamEvent, UUID> {

    @Query("SELECT j FROM JamEvent j WHERE j.eventDate >= :now " +
           "AND (j.createdBy.id = :userId " +
           "OR EXISTS (SELECT a FROM JamEventAttendee a WHERE a.jamEvent = j AND a.user.id = :userId)) " +
           "ORDER BY j.eventDate ASC")
    List<JamEvent> findUpcomingForUser(UUID userId, LocalDateTime now);

    List<JamEvent> findByBandIdAndEventDateAfterOrderByEventDateAsc(UUID bandId, LocalDateTime now);
}
