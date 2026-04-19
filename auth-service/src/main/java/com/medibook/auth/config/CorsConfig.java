package com.medibook.auth.config;

/*
 * CORS is handled entirely by the API Gateway (application.yml globalcors).
 * Do NOT add a CorsFilter bean here — it causes duplicate
 * Access-Control-Allow-Origin headers which browsers reject.
 */
public class CorsConfig {
    // intentionally empty
}