package com.bandswipe.jam.repository;

import com.bandswipe.jam.entity.JamEventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JamEventAttendeeRepository extends JpaRepository<JamEventAttendee, UUID> {

    Optional<JamEventAttendee> findByJamEventIdAndUserId(UUID eventId, UUID userId);
}
