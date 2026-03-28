package com.bandswipe.swipe.service;

import com.bandswipe.auth.repository.UserRepository;
import com.bandswipe.match.service.MatchService;
import com.bandswipe.shared.enums.SwipeDirection;
import com.bandswipe.shared.exception.DuplicateSwipeException;
import com.bandswipe.shared.exception.ResourceNotFoundException;
import com.bandswipe.swipe.dto.SwipeRequest;
import com.bandswipe.swipe.dto.SwipeResponse;
import com.bandswipe.swipe.entity.SwipeHistory;
import com.bandswipe.swipe.repository.SwipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;
    private final MatchService matchService;

    @Transactional
    public SwipeResponse swipe(UUID swiperId, SwipeRequest request) {
        if (swiperId.equals(request.targetUserId())) {
            throw new IllegalArgumentException("Cannot swipe on yourself");
        }

        if (swipeRepository.existsBySwiperIdAndSwipedId(swiperId, request.targetUserId())) {
            throw new DuplicateSwipeException("You have already swiped on this user");
        }

        var swiper = userRepository.findById(swiperId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + swiperId));
        var swiped = userRepository.findById(request.targetUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.targetUserId()));

        SwipeHistory swipeHistory = SwipeHistory.builder()
                .swiper(swiper)
                .swiped(swiped)
                .direction(request.direction())
                .build();

        swipeRepository.save(swipeHistory);

        if (request.direction() == SwipeDirection.RIGHT) {
            boolean mutual = swipeRepository.existsMutualRightSwipe(swiperId, request.targetUserId());
            if (mutual) {
                var match = matchService.createMatch(swiperId, request.targetUserId());
                return new SwipeResponse(true, match.getId());
            }
        }

        return new SwipeResponse(false, null);
    }
}
