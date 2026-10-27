package ru.trjamus.hw2.Console;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trjamus.hw2.Dao.UserInterface;
import ru.trjamus.hw2.entity.User;

import java.util.Scanner;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Menu — юнит-тесты (Mockito)")
class MenuTest {

    @Mock
    private UserInterface userDao;

    @Mock
    private Scanner scanner;

    @InjectMocks
    private Menu menu;

    @Nested
    @DisplayName("1. Добавить пользователя")
    class AddUser {

        @Test
        @DisplayName("Добавление пользователя с валидными данными")
        void testCallSave() {

            when(scanner.nextLine())
                    .thenReturn("1")
                    .thenReturn("Боб")
                    .thenReturn("qwe@qwe.ru")
                    .thenReturn("22")
                    .thenReturn("")
                    .thenReturn("0");

            menu.run();

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userDao).save(captor.capture());

            User saved = captor.getValue();
            assertThat(saved.getName()).isEqualTo("Боб");
            assertThat(saved.getEmail()).isEqualTo("qwe@qwe.ru");
            assertThat(saved.getAge()).isEqualTo(22);

        }

        @Test
        @DisplayName("Добавление пользователя без восраста")
        void testCallSaveWithNullAge() {

            when(scanner.nextLine())
                    .thenReturn("1")
                    .thenReturn("Боб")
                    .thenReturn("qwe@qwe.ru")
                    .thenReturn("")
                    .thenReturn("")
                    .thenReturn("0");

            menu.run();

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userDao).save(captor.capture());
            assertThat(captor.getValue().getAge()).isNull();

        }

        @Test
        @DisplayName("Попытка добавления с невалидным именем (пустым)")
        void testNoCallSaveUserNotValidName() {

            when(scanner.nextLine())
                    .thenReturn("1")
                    .thenReturn("   ")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);

        }

        @Test
        @DisplayName("Попытка добавления с невалидным email")
        void testNoCallSaveUserNotValidEmail() {

            when(scanner.nextLine())
                    .thenReturn("1")
                    .thenReturn("Боб")
                    .thenReturn("abracadabra")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);

        }

        @Test
        @DisplayName("Попытка добавления с невалидным возрастом (отрицательным)")
        void testNoCallSaveUserNegativeAge() {

            when(scanner.nextLine())
                    .thenReturn("1")
                    .thenReturn("Боб")
                    .thenReturn("qwe@qwe.ru")
                    .thenReturn("-1")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);

        }

    }

    @Nested
    @DisplayName("2. Найти пользователя по ID")
    class FindUser {

        @Test
        @DisplayName("Поиск с корректным UUID")
        void testCallFindById() {

            UUID id = UUID.randomUUID();
            when(scanner.nextLine())
                    .thenReturn("2")
                    .thenReturn(id.toString())
                    .thenReturn("")
                    .thenReturn("0");

            menu.run();

            verify(userDao).findById(id);

        }

        @Test
        @DisplayName("Поиск с невалидным UUID")
        void testNoCallFindByIdNotValidUuid() {
            when(scanner.nextLine())
                    .thenReturn("2")
                    .thenReturn("abracadabra")
                    .thenReturn("0")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);
        }
    }

    @Nested
    @DisplayName("3,4 Найти всех пользователей и Найти всех взрослых пользователей (18+)")
    class ListUsers {

        @Test
        @DisplayName("Поиск всех пользователей")
        void testCallFindAll() {

            when(scanner.nextLine())
                    .thenReturn("3")
                    .thenReturn("")
                    .thenReturn("0");

            menu.run();

            verify(userDao).findAll();

        }

        @Test
        @DisplayName("Поиск всех взрослых пользователей")
        void testCallFindAllAdults() {

            when(scanner.nextLine())
                    .thenReturn("4")
                    .thenReturn("")
                    .thenReturn("0");

            menu.run();

            verify(userDao).findAllAdults();

        }
    }


    @Nested
    @DisplayName("5. Обновить данные пользователя")
    class UpdateUser {

        @Test
        @DisplayName("Обновление всех полей")
        void testCallUpdateAllFields() {

            UUID id = UUID.randomUUID();
            when(scanner.nextLine())
                    .thenReturn("5")
                    .thenReturn(id.toString())
                    .thenReturn("Тот")
                    .thenReturn("asd@asd.ru")
                    .thenReturn("33")
                    .thenReturn("")
                    .thenReturn("0");

            menu.run();

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userDao).update(eq(id), captor.capture());

            User changes = captor.getValue();
            assertThat(changes.getName()).isEqualTo("Тот");
            assertThat(changes.getEmail()).isEqualTo("asd@asd.ru");
            assertThat(changes.getAge()).isEqualTo(33);

        }

        @Test
        @DisplayName("Частичное обновление")
        void testCallUpdatePartialFields() {

            UUID id = UUID.randomUUID();
            when(scanner.nextLine())
                    .thenReturn("5")
                    .thenReturn(id.toString())
                    .thenReturn("Тот")              // меняем только имя
                    .thenReturn("")                     // email пропущен
                    .thenReturn("")                     // возраст пропущен
                    .thenReturn("")                     // pause()
                    .thenReturn("0");

            menu.run();

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userDao).update(eq(id), captor.capture());

            User changes = captor.getValue();
            assertThat(changes.getName()).isEqualTo("Тот");
            assertThat(changes.getEmail()).isNull();
            assertThat(changes.getAge()).isNull();

        }

        @Test
        @DisplayName("Попытка обновления с невалидным email")
        void testNoCallUpdateNotValidEmail() {

            UUID id = UUID.randomUUID();
            when(scanner.nextLine())
                    .thenReturn("5")
                    .thenReturn(id.toString())
                    .thenReturn("Тот")
                    .thenReturn("qwe@qwe")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);

        }

        @Test
        @DisplayName("Попытка обновления с невалидном UUID")
        void testNoCallUpdateNotValidUuid() {

            when(scanner.nextLine())
                    .thenReturn("5")
                    .thenReturn("abracadabra")
                    .thenReturn("0")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);

        }

    }

    @Nested
    @DisplayName("6. Удалить пользователя по ID")
    class DeleteUser {

        @Test
        @DisplayName("Удаление пользователя с корректным UUID")
        void testCallDeleteById() {

            UUID id = UUID.randomUUID();
            when(scanner.nextLine())
                    .thenReturn("6")
                    .thenReturn(id.toString())
                    .thenReturn("")
                    .thenReturn("0");

            menu.run();

            verify(userDao).deleteById(id);

        }

        @Test
        @DisplayName("Удаление пользователя с невалидным UUID")
        void testNoCallDeleteByIdNotValidUuid() {

            when(scanner.nextLine())
                    .thenReturn("6")
                    .thenReturn("not-a-uuid")
                    .thenReturn("0")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);

        }

    }

    @Nested
    @DisplayName("Выход и неверный ввод")
    class ExitAndInvalidInput {

        @Test
        @DisplayName("Выход по 0")
        void testCallExit() {

            when(scanner.nextLine()).thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);
            verify(scanner, times(1)).nextLine();

        }

        @Test
        @DisplayName("Неверный пункт меню")
        void testNoCallAny() {

            when(scanner.nextLine())
                    .thenReturn("99")
                    .thenReturn("0");

            menu.run();

            verifyNoInteractions(userDao);

        }

    }

}