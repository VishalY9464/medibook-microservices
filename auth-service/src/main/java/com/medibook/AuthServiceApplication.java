package com.medibook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main class in package com.medibook so @SpringBootApplication scans:
 *   com.medibook.auth.*
 *   com.medibook.otp.*
 *   com.medibook.exception.*
 * All subpackages covered automatically.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
