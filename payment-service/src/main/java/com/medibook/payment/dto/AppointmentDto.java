package com.medibook.payment.dto;

import lombok.Data;

/**
 * Lightweight Appointment DTO received from appointment-service via Feign.
 * Only the fields payment-service needs — no shared entity.
 */
@Data
public class AppointmentDto {
    private int appointmentId;
    private int patientId;
    private int providerId;
    private String status;
}
