package com.medibook.payment.client;

import com.medibook.payment.dto.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client — payment-service calls appointment-service.
 * Replaces direct @Autowired AppointmentService from monolith.
 */
@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @GetMapping("/appointments/{appointmentId}")
    AppointmentDto getById(@PathVariable("appointmentId") int appointmentId);
}
