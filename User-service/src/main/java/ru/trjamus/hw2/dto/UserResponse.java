package ru.trjamus.hw2.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        @Schema(description = "Уникальный идентификатор", example = "555e84444-e22b-44d4-a777-446655442200")
        UUID id,

        @Schema(description = "Имя", example = "Bob")
        String name,

        @Schema(description = "Email", example = "example@example.ru")
        String email,

        @Schema(description = "Возраст", example = "22")
        Integer age,

        @Schema(description = "Дата создания", example = "2027-01-01T00:00:00")
        LocalDateTime createdAt
) {}
