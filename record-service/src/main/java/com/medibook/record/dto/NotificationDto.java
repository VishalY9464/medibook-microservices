package com.medibook.record.dto;

import lombok.Data;

/**
 * DTO for sending a notification request to notification-service via Feign.
 * Matches NotificationRequest in notification-service exactly.
 */
@Data
public class NotificationDto {
    private int recipientId;
    private String type;
    private String title;
    private String message;
    private String channel;
    private int relatedId;
    private String relatedType;
}
