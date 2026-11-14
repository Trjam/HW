package ru.trjamus.hw2.notification.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trjamus.hw2.notification.service.EmailService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody SendRequest request) {
        switch (request.getType()) {
            case "CREATED" -> emailService.sendCreatedEmail(request.getEmail());
            case "DELETED" -> emailService.sendDeletedEmail(request.getEmail());
            default -> throw new IllegalArgumentException("Неизвестный тип: " + request.getType());
        }
        return ResponseEntity.ok().build();
    }

    @Data
    public static class SendRequest {
        @NotBlank
        private String type;

        @NotBlank
        @Email
        private String email;
    }
}