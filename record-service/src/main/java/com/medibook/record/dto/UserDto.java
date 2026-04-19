package com.medibook.record.dto;

import lombok.Data;

/**
 * Lightweight User DTO received from auth-service via Feign.
 * Used by FollowUpReminderScheduler to get patient name for notification message.
 */
@Data
public class UserDto {
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
}
