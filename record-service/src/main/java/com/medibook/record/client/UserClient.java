package com.medibook.record.client;

import com.medibook.record.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client — record-service calls auth-service.
 * Used by FollowUpReminderScheduler to get patient name for reminder message.
 * Replaces direct @Autowired UserRepository from monolith.
 */
@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/auth/profile/{userId}")
    UserDto getUserById(@PathVariable("userId") int userId);
}
