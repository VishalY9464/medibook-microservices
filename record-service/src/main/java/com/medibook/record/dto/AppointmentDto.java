package com.medibook.record.dto;

import lombok.Data;

/**
 * Lightweight Appointment DTO received from appointment-service via Feign.
 * Only the fields record-service needs — no shared entity.
 */
@Data
public class AppointmentDto {
    private int appointmentId;
    private int patientId;
    private int providerId;
    private String status;
}
