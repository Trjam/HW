package ru.trjamus.hw2.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, UUID id) {

        super(resource + " с ID " + id + " не найден");

    }

}
