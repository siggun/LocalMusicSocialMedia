package com.bandswipe.jam.service;

import com.bandswipe.auth.entity.User;
import com.bandswipe.auth.repository.UserRepository;
import com.bandswipe.band.entity.Band;
import com.bandswipe.band.repository.BandRepository;
import com.bandswipe.jam.dto.CreateJamRequest;
import com.bandswipe.jam.dto.JamEventResponse;
import com.bandswipe.jam.entity.JamEvent;
import com.bandswipe.jam.entity.JamEventAttendee;
import com.bandswipe.jam.repository.JamEventAttendeeRepository;
import com.bandswipe.jam.repository.JamEventRepository;
import com.bandswipe.shared.enums.RsvpStatus;
import com.bandswipe.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JamEventService {

    private final JamEventRepository jamEventRepository;
    private final JamEventAttendeeRepository attendeeRepository;
    private final UserRepository userRepository;
    private final BandRepository bandRepository;

    @Transactional
    public JamEventResponse createJam(UUID creatorId, CreateJamRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Band band = null;
        if (request.getBandId() != null) {
            band = bandRepository.findById(request.getBandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Band not found"));
        }

        JamEvent event = JamEvent.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .eventDate(request.getEventDate())
                .createdBy(creator)
                .band(band)
                .build();

        event = jamEventRepository.save(event);

        // Creator auto-RSVPs as GOING
        JamEventAttendee creatorRsvp = JamEventAttendee.builder()
                .jamEvent(event)
                .user(creator)
                .status(RsvpStatus.GOING)
                .build();
        event.getAttendees().add(creatorRsvp);

        event = jamEventRepository.save(event);
        return JamEventResponse.fromEntity(event);
    }

    @Transactional(readOnly = true)
    public List<JamEventResponse> getUpcomingJams(UUID userId) {
        return jamEventRepository.findUpcomingForUser(userId, LocalDateTime.now()).stream()
                .map(JamEventResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public JamEventResponse getJam(UUID jamId) {
        JamEvent event = jamEventRepository.findById(jamId)
                .orElseThrow(() -> new ResourceNotFoundException("Jam session not found"));
        return JamEventResponse.fromEntity(event);
    }

    @Transactional
    public JamEventResponse rsvp(UUID jamId, UUID userId, RsvpStatus status) {
        JamEvent event = jamEventRepository.findById(jamId)
                .orElseThrow(() -> new ResourceNotFoundException("Jam session not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JamEventAttendee attendee = attendeeRepository.findByJamEventIdAndUserId(jamId, userId)
                .orElse(null);

        if (attendee != null) {
            attendee.setStatus(status);
            attendee.setRespondedAt(LocalDateTime.now());
        } else {
            attendee = JamEventAttendee.builder()
                    .jamEvent(event)
                    .user(user)
                    .status(status)
                    .build();
            event.getAttendees().add(attendee);
        }

        event = jamEventRepository.save(event);
        return JamEventResponse.fromEntity(event);
    }
}
