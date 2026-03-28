package com.bandswipe.jam.entity;

import com.bandswipe.auth.entity.User;
import com.bandswipe.band.entity.Band;
import com.bandswipe.shared.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jam_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JamEvent extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_name")
    private String locationName;

    private Double latitude;

    private Double longitude;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "band_id")
    private Band band;

    @Builder.Default
    @OneToMany(mappedBy = "jamEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JamEventAttendee> attendees = new ArrayList<>();
}
