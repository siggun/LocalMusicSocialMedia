package com.bandswipe.jam.controller;

import com.bandswipe.auth.entity.User;
import com.bandswipe.jam.dto.CreateJamRequest;
import com.bandswipe.jam.dto.JamEventResponse;
import com.bandswipe.jam.dto.RsvpRequest;
import com.bandswipe.jam.service.JamEventService;
import com.bandswipe.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jams")
@RequiredArgsConstructor
public class JamEventController {

    private final JamEventService jamEventService;

    @PostMapping
    public ResponseEntity<ApiResponse<JamEventResponse>> createJam(
            Authentication authentication,
            @Valid @RequestBody CreateJamRequest request) {

        User user = (User) authentication.getPrincipal();
        JamEventResponse jam = jamEventService.createJam(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Jam session created", jam));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JamEventResponse>>> getUpcomingJams(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        List<JamEventResponse> jams = jamEventService.getUpcomingJams(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Jams retrieved", jams));
    }

    @GetMapping("/{jamId}")
    public ResponseEntity<ApiResponse<JamEventResponse>> getJam(
            @PathVariable UUID jamId) {

        JamEventResponse jam = jamEventService.getJam(jamId);
        return ResponseEntity.ok(ApiResponse.ok("Jam retrieved", jam));
    }

    @PostMapping("/{jamId}/rsvp")
    public ResponseEntity<ApiResponse<JamEventResponse>> rsvp(
            Authentication authentication,
            @PathVariable UUID jamId,
            @Valid @RequestBody RsvpRequest request) {

        User user = (User) authentication.getPrincipal();
        JamEventResponse jam = jamEventService.rsvp(jamId, user.getId(), request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("RSVP recorded", jam));
    }
}
