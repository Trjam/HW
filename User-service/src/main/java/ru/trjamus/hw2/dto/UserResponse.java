package ru.trjamus.hw2.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {}
