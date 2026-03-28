package com.bandswipe.notification.controller;

import com.bandswipe.auth.entity.User;
import com.bandswipe.notification.dto.NotificationResponse;
import com.bandswipe.notification.dto.RegisterTokenRequest;
import com.bandswipe.notification.dto.UnreadCountResponse;
import com.bandswipe.notification.service.NotificationService;
import com.bandswipe.notification.service.RealtimeNotificationService;
import com.bandswipe.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final RealtimeNotificationService realtimeNotificationService;

    @PutMapping("/token")
    public ResponseEntity<ApiResponse<Void>> registerToken(
            @Valid @RequestBody RegisterTokenRequest request,
            Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        notificationService.registerToken(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Token registered", null));
    }

    @DeleteMapping("/token/{token}")
    public ResponseEntity<ApiResponse<Void>> removeToken(
            @PathVariable String token,
            Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        notificationService.removeToken(userId, token);
        return ResponseEntity.ok(ApiResponse.ok("Token removed", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        List<NotificationResponse> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.ok(new UnreadCountResponse(count)));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable UUID notificationId,
            Authentication authentication) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        return realtimeNotificationService.subscribe(userId);
    }

    private UUID getCurrentUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
