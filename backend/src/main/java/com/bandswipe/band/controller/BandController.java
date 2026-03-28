package com.bandswipe.band.controller;

import com.bandswipe.auth.entity.User;
import com.bandswipe.band.dto.AddMemberRequest;
import com.bandswipe.band.dto.BandResponse;
import com.bandswipe.band.dto.CreateBandRequest;
import com.bandswipe.band.service.BandService;
import com.bandswipe.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bands")
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BandResponse>> createBand(
            Authentication authentication,
            @Valid @RequestBody CreateBandRequest request) {

        User user = (User) authentication.getPrincipal();
        BandResponse band = bandService.createBand(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Band created", band));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BandResponse>>> getUserBands(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        List<BandResponse> bands = bandService.getUserBands(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Bands retrieved", bands));
    }

    @GetMapping("/{bandId}")
    public ResponseEntity<ApiResponse<BandResponse>> getBand(
            Authentication authentication,
            @PathVariable UUID bandId) {

        User user = (User) authentication.getPrincipal();
        BandResponse band = bandService.getBand(bandId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Band retrieved", band));
    }

    @PostMapping("/{bandId}/members")
    public ResponseEntity<ApiResponse<BandResponse>> addMember(
            Authentication authentication,
            @PathVariable UUID bandId,
            @Valid @RequestBody AddMemberRequest request) {

        User user = (User) authentication.getPrincipal();
        BandResponse band = bandService.addMember(bandId, user.getId(), request.getUserId(), request.getRole());
        return ResponseEntity.ok(ApiResponse.ok("Member added", band));
    }

    @DeleteMapping("/{bandId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            Authentication authentication,
            @PathVariable UUID bandId,
            @PathVariable UUID userId) {

        User user = (User) authentication.getPrincipal();
        bandService.removeMember(bandId, user.getId(), userId);
        return ResponseEntity.ok(ApiResponse.ok("Member removed", null));
    }
}
