package ru.trjamus.hw2.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trjamus.hw2.notification.service.EmailService;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Уведомления", description = "Ручная отправка email-уведомлений")
public class NotificationController {

    private final EmailService emailService;

    @Operation(summary = "Отправить уведомление",
            description = "Отправляет письмо: CREATED — «аккаунт создан», DELETED — «аккаунт удалён»")
    @ApiResponse(responseCode = "200", description = "Письмо отправлено")
    @ApiResponse(responseCode = "400", description = "Невалидные данные")
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
    @Schema(description = "Запрос на отправку уведомления")
    public static class SendRequest {
        @NotBlank
        @Schema(description = "Тип уведомления", example = "CREATED",
                allowableValues = {"CREATED", "DELETED"})
        private String type;

        @NotBlank
        @Email
        @Schema(description = "Email получателя", example = "example@example.ru")
        private String email;
    }
}