package ru.trjamus.hw2.Utils;

import ru.trjamus.hw2.exception.ValidationException;

public final class Validator {

    private Validator() {}

    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        return name.trim();
    }

    public static String validateEmail(String email) {
        if (email == null) {
            throw new ValidationException("Некорректный email: null");
        }
        String trimmedEmail = email.trim();
        if (!trimmedEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ValidationException("Некорректный email: " + email);
        }
        return trimmedEmail;
    }

    public static Integer validateAge(Integer age) {
        if (age != null && (age < 0 || age > 150)) {
            throw new ValidationException("Возраст должен быть от 0 до 150");
        }
        return age;
    }
}
