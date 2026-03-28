package com.bandswipe.notification.dto;

import com.bandswipe.notification.entity.Notification;
import com.bandswipe.notification.entity.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String title,
        String body,
        String dataJson,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getDataJson(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
