package com.bandswipe.jam.dto;

import com.bandswipe.jam.entity.JamEvent;
import com.bandswipe.jam.entity.JamEventAttendee;
import com.bandswipe.shared.enums.RsvpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class JamEventResponse {

    private UUID id;
    private String title;
    private String description;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime eventDate;
    private UUID createdBy;
    private String createdByName;
    private UUID bandId;
    private String bandName;
    private List<AttendeeInfo> attendees;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AttendeeInfo {
        private UUID userId;
        private String displayName;
        private RsvpStatus status;
        private LocalDateTime respondedAt;
    }

    public static JamEventResponse fromEntity(JamEvent event) {
        List<AttendeeInfo> attendeeInfos = event.getAttendees().stream()
                .map(a -> AttendeeInfo.builder()
                        .userId(a.getUser().getId())
                        .displayName(a.getUser().getDisplayName())
                        .status(a.getStatus())
                        .respondedAt(a.getRespondedAt())
                        .build())
                .toList();

        return JamEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .locationName(event.getLocationName())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .eventDate(event.getEventDate())
                .createdBy(event.getCreatedBy().getId())
                .createdByName(event.getCreatedBy().getDisplayName())
                .bandId(event.getBand() != null ? event.getBand().getId() : null)
                .bandName(event.getBand() != null ? event.getBand().getName() : null)
                .attendees(attendeeInfos)
                .createdAt(event.getCreatedAt())
                .build();
    }
}
