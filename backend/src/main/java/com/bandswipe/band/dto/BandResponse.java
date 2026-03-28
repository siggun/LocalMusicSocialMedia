package com.bandswipe.band.dto;

import com.bandswipe.band.entity.Band;
import com.bandswipe.band.entity.BandMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class BandResponse {

    private UUID id;
    private String name;
    private String genre;
    private String bio;
    private String photoUrl;
    private UUID createdBy;
    private List<MemberInfo> members;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberInfo {
        private UUID userId;
        private String displayName;
        private String role;
        private LocalDateTime joinedAt;
    }

    public static BandResponse fromEntity(Band band) {
        List<MemberInfo> memberInfos = band.getMembers().stream()
                .map(m -> MemberInfo.builder()
                        .userId(m.getUser().getId())
                        .displayName(m.getUser().getDisplayName())
                        .role(m.getRole())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .toList();

        return BandResponse.builder()
                .id(band.getId())
                .name(band.getName())
                .genre(band.getGenre())
                .bio(band.getBio())
                .photoUrl(band.getPhotoUrl())
                .createdBy(band.getCreatedBy().getId())
                .members(memberInfos)
                .createdAt(band.getCreatedAt())
                .build();
    }
}
