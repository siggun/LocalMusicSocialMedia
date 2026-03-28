package com.bandswipe.match.controller;

import com.bandswipe.auth.entity.User;
import com.bandswipe.match.dto.MatchResponse;
import com.bandswipe.match.service.MatchService;
import com.bandswipe.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatches(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<MatchResponse> matches = matchService.getMatches(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Matches retrieved", matches));
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<ApiResponse<Void>> unmatch(
            Authentication authentication,
            @PathVariable UUID matchId) {

        User user = (User) authentication.getPrincipal();
        matchService.unmatch(matchId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Unmatched successfully", null));
    }
}
