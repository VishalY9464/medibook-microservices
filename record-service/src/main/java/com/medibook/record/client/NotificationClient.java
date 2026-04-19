package com.medibook.record.client;

import com.medibook.record.dto.NotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client — record-service calls notification-service.
 * Used by FollowUpReminderScheduler to send email reminders to patients.
 * Replaces direct @Autowired NotificationService from monolith.
 */
@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/send")
    void send(@RequestBody NotificationDto request);
}
