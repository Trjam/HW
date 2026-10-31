package ru.trjamus.hw2.Utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.trjamus.hw2.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Юнит-тесты для валидатора")
class ValidatorTest {

    @Nested
    @DisplayName("Валидация имени")
    class ValidateName {

        @Test
        @DisplayName("Валидация имени с пробелами")
        void testValidNameWithSpaces() {
            assertEquals("Боб", Validator.validateName("  Боб  "));
        }

        @Test
        @DisplayName("Исключение для пустой строки (пробелы)")
        void testNotValidNameBlank() {
            assertThrows(ValidationException.class, () -> Validator.validateName("   "));
        }

        @Test
        @DisplayName("Исключение для null")
        void testNotValidNameNull() {
            assertThrows(ValidationException.class, () -> Validator.validateName(null));
        }
    }

    @Nested
    @DisplayName("Валидация email")
    class ValidateEmail {

        @Test
        @DisplayName("Валидация email с пробелами")
        void testValidEmailWithSpaces() {
            assertEquals("qwe@qwe.ru", Validator.validateEmail(" qwe@qwe.ru "));
        }

        @Test
        @DisplayName("Исключение, если нет символа @")
        void testNotValidEmailWithoutSymbol() {
            assertThrows(ValidationException.class, () -> Validator.validateEmail("abracadabra"));
        }

        @Test
        @DisplayName("Исключение, если нет домена")
        void testNotValidEmailWithoutDomain() {
            assertThrows(ValidationException.class, () -> Validator.validateEmail("qwe@qwe"));
        }

        @Test
        @DisplayName("Исключение для null")
        void testNotValidEmailNull() {
            assertThrows(ValidationException.class, () -> Validator.validateEmail(null));
        }
    }

    @Nested
    @DisplayName("Валидация возраста")
    class ValidateAge {

        @Test
        @DisplayName("Валидация возраста, передаем null")
        void testValidAgeNull() {
            assertNull(Validator.validateAge(null));
        }

        @Test
        @DisplayName("Валидация возраста, целое число")
        void testValidAge() {
            assertEquals(22, Validator.validateAge(22));
        }

        @Test
        @DisplayName("Исключение, отрицательное целое число")
        void testNotValidAgeNegative() {
            assertThrows(ValidationException.class, () -> Validator.validateAge(-1));
        }

        @Test
        @DisplayName("Исключение для возраста больше 150")
        void testNotValidAgeTooLarge() {
            assertThrows(ValidationException.class, () -> Validator.validateAge(151));
        }
    }
}