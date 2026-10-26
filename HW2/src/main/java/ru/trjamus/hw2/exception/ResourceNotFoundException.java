package ru.trjamus.hw2.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entityName, Object id) {
        super(entityName + " не найден: " + id);
    }
}
