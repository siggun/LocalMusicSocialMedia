package com.bandswipe.band.entity;

import com.bandswipe.auth.entity.User;
import com.bandswipe.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Band extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String genre;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandMember> members = new ArrayList<>();
}
