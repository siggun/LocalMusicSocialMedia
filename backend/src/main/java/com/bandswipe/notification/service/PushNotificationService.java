package com.bandswipe.notification.service;

import com.bandswipe.notification.entity.DeviceToken;
import com.bandswipe.notification.repository.DeviceTokenRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void sendPushNotification(UUID userId, String title, String body, Map<String, String> data) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Firebase is not initialized. Skipping push notification for user {}", userId);
            return;
        }

        List<DeviceToken> deviceTokens = deviceTokenRepository.findByUserId(userId);
        if (deviceTokens.isEmpty()) {
            log.debug("No device tokens found for user {}", userId);
            return;
        }

        for (DeviceToken deviceToken : deviceTokens) {
            try {
                Message.Builder messageBuilder = Message.builder()
                        .setToken(deviceToken.getToken())
                        .setNotification(
                                Notification.builder()
                                        .setTitle(title)
                                        .setBody(body)
                                        .build()
                        );

                if (data != null && !data.isEmpty()) {
                    messageBuilder.putAllData(data);
                }

                FirebaseMessaging.getInstance().sendAsync(messageBuilder.build());
                log.debug("Push notification sent to device token {} for user {}", deviceToken.getToken(), userId);

            } catch (Exception e) {
                if (e instanceof FirebaseMessagingException fme
                        && fme.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    log.info("Removing invalid/expired device token for user {}", userId);
                    deviceTokenRepository.deleteByUserIdAndToken(userId, deviceToken.getToken());
                } else {
                    log.error("Failed to send push notification to user {} with token {}: {}",
                            userId, deviceToken.getToken(), e.getMessage());
                }
            }
        }
    }

    public void sendToMultipleUsers(List<UUID> userIds, String title, String body, Map<String, String> data) {
        for (UUID userId : userIds) {
            sendPushNotification(userId, title, body, data);
        }
    }
}
