package com.medibook.notification.client;

import com.medibook.notification.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client — notification-service calls auth-service.
 * Replaces direct @Autowired UserRepository from monolith.
 * Used to look up real user email before sending EMAIL notifications.
 */
@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/auth/profile/{userId}")
    UserDto getUserById(@PathVariable("userId") int userId);
}
