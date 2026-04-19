package com.medibook.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client — review-service calls provider-service.
 * Replaces direct @Autowired ProviderService from monolith.
 * Used to update doctor avgRating after every review change.
 */
@FeignClient(name = "provider-service")
public interface ProviderClient {

    @PutMapping("/providers/{providerId}/rating")
    void updateRating(@PathVariable("providerId") int providerId,
                      @RequestParam("avgRating") double avgRating);
}
