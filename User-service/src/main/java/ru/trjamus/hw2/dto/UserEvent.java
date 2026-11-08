package ru.trjamus.hw2.dto;

public record UserEvent(
        String type,   // "CREATED" или "DELETED"
        String email
) {}