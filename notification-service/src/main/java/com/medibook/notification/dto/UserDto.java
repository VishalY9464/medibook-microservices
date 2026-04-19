package com.medibook.notification.dto;

import lombok.Data;

/**
 * Lightweight User DTO received from auth-service via Feign.
 * Only fields notification-service needs — no shared entity.
 */
@Data
public class UserDto {
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean isActive;
}
