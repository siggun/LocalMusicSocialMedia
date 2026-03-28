package com.bandswipe.match.dto;

import com.bandswipe.match.entity.Match;
import com.bandswipe.profile.dto.ProfileResponse;
import com.bandswipe.profile.entity.MusicianProfile;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchResponse(
        UUID matchId,
        ProfileResponse matchedUser,
        LocalDateTime matchedAt
) {

    public static MatchResponse fromEntity(Match match, UUID currentUserId, MusicianProfile matchedProfile) {
        return new MatchResponse(
                match.getId(),
                ProfileResponse.fromEntity(matchedProfile),
                match.getMatchedAt()
        );
    }
}
