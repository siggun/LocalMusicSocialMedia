package com.bandswipe.notification.service;

import com.bandswipe.auth.entity.User;
import com.bandswipe.auth.repository.UserRepository;
import com.bandswipe.notification.dto.NotificationResponse;
import com.bandswipe.notification.dto.RegisterTokenRequest;
import com.bandswipe.notification.entity.DeviceToken;
import com.bandswipe.notification.entity.Notification;
import com.bandswipe.notification.entity.NotificationType;
import com.bandswipe.notification.repository.DeviceTokenRepository;
import com.bandswipe.notification.repository.NotificationRepository;
import com.bandswipe.profile.entity.MusicianProfile;
import com.bandswipe.profile.repository.ProfileRepository;
import com.bandswipe.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;
    private final RealtimeNotificationService realtimeNotificationService;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    public void registerToken(UUID userId, RegisterTokenRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        var existingToken = deviceTokenRepository.findByUserIdAndToken(userId, req.token());

        if (existingToken.isPresent()) {
            DeviceToken token = existingToken.get();
            token.setDeviceType(req.deviceType());
            deviceTokenRepository.save(token);
            log.debug("Updated device token for user {}", userId);
        } else {
            DeviceToken token = DeviceToken.builder()
                    .user(user)
                    .token(req.token())
                    .deviceType(req.deviceType())
                    .build();
            deviceTokenRepository.save(token);
            log.debug("Registered new device token for user {}", userId);
        }
    }

    public void removeToken(UUID userId, String token) {
        deviceTokenRepository.deleteByUserIdAndToken(userId, token);
        log.debug("Removed device token for user {}", userId);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public NotificationResponse notify(UUID userId, NotificationType type, String title, String body,
                                       Map<String, String> data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        String dataJson = null;
        if (data != null && !data.isEmpty()) {
            try {
                dataJson = objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize notification data to JSON: {}", e.getMessage());
            }
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .body(body)
                .dataJson(dataJson)
                .build();

        notification = notificationRepository.save(notification);

        NotificationResponse response = NotificationResponse.fromEntity(notification);

        realtimeNotificationService.sendToUser(userId, response);

        try {
            pushNotificationService.sendPushNotification(userId, title, body, data);
        } catch (Exception e) {
            log.error("Failed to send push notification to user {}: {}", userId, e.getMessage());
        }

        return response;
    }

    public void notifyMatch(UUID userId1, UUID userId2, UUID matchId) {
        MusicianProfile profile1 = profileRepository.findByUserId(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", userId1));
        MusicianProfile profile2 = profileRepository.findByUserId(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("MusicianProfile", userId2));

        Map<String, String> dataForUser1 = Map.of(
                "matchId", matchId.toString(),
                "matchedUserId", userId2.toString()
        );

        Map<String, String> dataForUser2 = Map.of(
                "matchId", matchId.toString(),
                "matchedUserId", userId1.toString()
        );

        notify(userId1, NotificationType.MATCH,
                "It's a Match! \uD83C\uDFB5",
                profile2.getDisplayName() + " wants to jam with you!",
                dataForUser1);

        notify(userId2, NotificationType.MATCH,
                "It's a Match! \uD83C\uDFB5",
                profile1.getDisplayName() + " wants to jam with you!",
                dataForUser2);
    }
}
