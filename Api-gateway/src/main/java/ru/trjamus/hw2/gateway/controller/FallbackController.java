package ru.trjamus.hw2.gateway.controller;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
@RequiredArgsConstructor
public class FallbackController {

    private final Tracer tracer;

    @RequestMapping("/users")
    public ResponseEntity<Map<String, Object>> usersFallback() {
        log.warn("Fallback: user-service недоступен");
        return buildFallback("user-service");
    }

    @RequestMapping("/notifications")
    public ResponseEntity<Map<String, Object>> notificationsFallback() {
        log.warn("Fallback: notification-service недоступен");
        return buildFallback("notification-service");
    }

    private ResponseEntity<Map<String, Object>> buildFallback(String serviceName) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 503);
        body.put("error", "Service Unavailable");
        body.put("message", "Сервис «" + serviceName + "» временно недоступен. Попробуйте позже.");
        body.put("service", serviceName);

        // цепляем traceId, чтоб искалось легче
        var span = tracer.currentSpan();
        if (span != null) {
            body.put("traceId", span.context().traceId());
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}