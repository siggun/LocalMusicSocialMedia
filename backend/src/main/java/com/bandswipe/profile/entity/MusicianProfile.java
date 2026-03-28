package com.bandswipe.profile.entity;

import com.bandswipe.auth.entity.User;
import com.bandswipe.shared.BaseEntity;
import com.bandswipe.shared.enums.Availability;
import com.bandswipe.shared.enums.SkillLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "musician_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicianProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level")
    private SkillLevel skillLevel;

    @Convert(converter = AvailabilityConverter.class)
    @Column(name = "availability")
    private Set<Availability> availability;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "audio_intro_url")
    private String audioIntroUrl;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    private Double latitude;

    private Double longitude;

    @Builder.Default
    @Column(name = "max_distance_miles")
    private Integer maxDistanceMiles = 25;

    @Column(name = "soundcloud_url")
    private String soundcloudUrl;

    @Column(name = "spotify_url")
    private String spotifyUrl;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "bandcamp_url")
    private String bandcampUrl;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "profile_instruments",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private Set<Instrument> instruments = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "profile_genres",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
}
