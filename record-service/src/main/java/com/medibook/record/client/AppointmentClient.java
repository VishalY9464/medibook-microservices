package com.medibook.record.client;

import com.medibook.record.dto.AppointmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client — record-service calls appointment-service.
 * Replaces direct @Autowired AppointmentRepository from monolith.
 * Used to verify appointment exists and is COMPLETED before creating record.
 */
@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @GetMapping("/appointments/{appointmentId}")
    AppointmentDto getById(@PathVariable("appointmentId") int appointmentId);
}
