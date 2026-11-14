package ru.trjamus.hw2.notification.dto;

public record UserEvent(
        String type,
        String email
) {}