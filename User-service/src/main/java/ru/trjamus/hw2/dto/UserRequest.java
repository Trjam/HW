package ru.trjamus.hw2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание/обновление пользователя")
public class UserRequest {

    @NotBlank(message = "Имя не может быть пустым")
    @Schema(description = "Имя пользователя", example = "Bob", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$", message = "Email должен быть вида example@example.ru")
    @Schema(example = "example@example.ru", description = "Email пользователя", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 150, message = "Возраст не может быть больше 150")
    @Schema(description = "Возраст (опционально)", example = "22")
    private Integer age;
}
