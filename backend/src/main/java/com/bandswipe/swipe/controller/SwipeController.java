package com.bandswipe.swipe.controller;

import com.bandswipe.auth.entity.User;
import com.bandswipe.shared.dto.ApiResponse;
import com.bandswipe.swipe.dto.SwipeRequest;
import com.bandswipe.swipe.dto.SwipeResponse;
import com.bandswipe.swipe.service.SwipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/swipe")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping
    public ResponseEntity<ApiResponse<SwipeResponse>> swipe(
            Authentication authentication,
            @Valid @RequestBody SwipeRequest request) {

        User user = (User) authentication.getPrincipal();
        SwipeResponse response = swipeService.swipe(user.getId(), request);

        String message = response.matched() ? "It's a match!" : "Swipe recorded";
        return ResponseEntity.ok(ApiResponse.ok(message, response));
    }
}
