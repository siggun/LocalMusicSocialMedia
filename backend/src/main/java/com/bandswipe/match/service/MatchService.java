package com.bandswipe.match.service;

import com.bandswipe.auth.repository.UserRepository;
import com.bandswipe.match.dto.MatchResponse;
import com.bandswipe.match.entity.Match;
import com.bandswipe.match.repository.MatchRepository;
import com.bandswipe.profile.entity.MusicianProfile;
import com.bandswipe.profile.repository.ProfileRepository;
import com.bandswipe.notification.service.NotificationService;
import com.bandswipe.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final NotificationService notificationService;

    @Transactional
    public Match createMatch(UUID userId1, UUID userId2) {
        // Ensure user1_id < user2_id for uniqueness
        UUID first = userId1.compareTo(userId2) < 0 ? userId1 : userId2;
        UUID second = userId1.compareTo(userId2) < 0 ? userId2 : userId1;

        var user1 = userRepository.findById(first)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + first));
        var user2 = userRepository.findById(second)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + second));

        Match match = Match.builder()
                .user1(user1)
                .user2(user2)
                .build();

        match = matchRepository.save(match);

        try {
            notificationService.notifyMatch(userId1, userId2, match.getId());
        } catch (Exception e) {
            log.error("Failed to send match notification for match {}: {}", match.getId(), e.getMessage(), e);
        }

        return match;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatches(UUID userId) {
        List<Match> matches = matchRepository.findActiveMatchesForUser(userId);

        return matches.stream()
                .map(match -> {
                    UUID otherUserId = match.getUser1().getId().equals(userId)
                            ? match.getUser2().getId()
                            : match.getUser1().getId();

                    MusicianProfile matchedProfile = profileRepository.findByUserId(otherUserId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Profile not found for user: " + otherUserId));

                    return MatchResponse.fromEntity(match, userId, matchedProfile);
                })
                .toList();
    }

    @Transactional
    public void unmatch(UUID matchId, UUID userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));

        boolean isParticipant = match.getUser1().getId().equals(userId)
                || match.getUser2().getId().equals(userId);

        if (!isParticipant) {
            throw new ResourceNotFoundException("Match not found: " + matchId);
        }

        match.setActive(false);
        matchRepository.save(match);
    }
}
