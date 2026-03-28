package com.bandswipe.discovery.controller;

import com.bandswipe.auth.entity.User;
import com.bandswipe.discovery.dto.DiscoveryProfileResponse;
import com.bandswipe.discovery.service.DiscoveryService;
import com.bandswipe.shared.dto.ApiResponse;
import com.bandswipe.shared.enums.SkillLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<DiscoveryProfileResponse>>> getFeed(
            Authentication authentication,
            @RequestParam(defaultValue = "25") Integer maxDistance,
            @RequestParam(required = false) Set<Integer> genres,
            @RequestParam(required = false) Set<Integer> instruments,
            @RequestParam(required = false) SkillLevel skillLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        User user = (User) authentication.getPrincipal();

        Page<DiscoveryProfileResponse> feed = discoveryService.getFeed(
                user.getId(),
                maxDistance,
                genres,
                instruments,
                skillLevel,
                PageRequest.of(page, size)
        );

        return ResponseEntity.ok(ApiResponse.ok("Discovery feed retrieved", feed));
    }
}
