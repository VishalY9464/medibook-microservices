package com.medibook.review.dto;

import lombok.Data;

/**
 * Lightweight Appointment DTO received from appointment-service via Feign.
 */
@Data
public class AppointmentDto {
    private int appointmentId;
    private int patientId;
    private int providerId;
    private String status;
}
