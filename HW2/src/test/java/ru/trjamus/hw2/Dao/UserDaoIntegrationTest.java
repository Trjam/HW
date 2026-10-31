package ru.trjamus.hw2.Dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.trjamus.hw2.entity.User;
import ru.trjamus.hw2.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Интеграционные тесты (Testcontainers)")
class UserDaoIntegrationTest extends AbstractDaoIntegrationTest {

    private static final String USERS_TABLE = "\"user\".\"Users\"";
    private UserInterface userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDao(sessionFactory);
        truncateTables(USERS_TABLE);
    }

    @Nested
    @DisplayName("Добавление нового пользователя (save)")
    class SaveTests {

        @Test
        @DisplayName("Добавление нового пользователя")
        void testAddNewUser() {

            User user = new User("Боб", "qwe@qwe.ru", 25);

            userDao.save(user);

            assertThat(user.getId()).isNotNull();
            assertThat(user.getCreatedAt()).isNotNull();

        }

        @Test
        @DisplayName("Исключение при попытке сохранить дубликат email")
        void testAddNewUserWithDuplicateEmail() {

            userDao.save(new User("Боб", "qwe@qwe.ru", 22));

            User duplicate = new User("Тот", "qwe@qwe.ru", 30);

            assertThatThrownBy(() -> userDao.save(duplicate))
                    .isInstanceOf(Exception.class);

        }

    }

    @Nested
    @DisplayName("Поиск (find)")
    class FindTests {

        @Test
        @DisplayName("Поиск пользователя по ID")
        void testFindById() {

            User user = new User("Боб", "qwe@qwe.ru", 22);
            userDao.save(user);

            Optional<User> found = userDao.findById(user.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Боб");

        }

        @Test
        @DisplayName("Поиск по несуществующему ID")
        void testFindByIdThatNotExist() {

            assertThat(userDao.findById(UUID.randomUUID())).isEmpty();

        }


        @Test
        @DisplayName("Поиск всех пользователей в пустой базе")
        void testFindAllNoUsers() {

            assertThat(userDao.findAll()).isEmpty();

        }

        @Test
        @DisplayName("Поиск всех пользователей")
        void testFindAll() {

            userDao.save(new User("Боб", "qwe@qwe.ru", 22));
            userDao.save(new User("Тот", "asd@asd.ru", 33));

            List<User> all = userDao.findAll();

            assertThat(all).hasSize(2);
            assertThat(all).extracting(User::getEmail)
                    .containsExactlyInAnyOrder("qwe@qwe.ru", "asd@asd.ru");

        }

        @Test
        @DisplayName("Поиск всем взрослых пользователей (18+)")
        void testFindAllAdults() {

            userDao.save(new User("Боб17", "qwe@qwe.ru", 17));
            userDao.save(new User("Боб18", "as@asd.ru", 18));
            userDao.save(new User("Боб19", "zxc@zxc.ru", 19));
            userDao.save(new User("Боб100",   "zzzz@zzzz.ru",   100));

            List<User> adults = userDao.findAllAdults();

            assertThat(adults).hasSize(3);

            assertThat(adults)
                    .extracting(User::getAge)
                    .containsExactlyInAnyOrder(18, 19, 100);
        }

        @Test
        @DisplayName("Поиск всех взрослых пользователей (18+), игнорирование null")
        void testFindAllAdultsIgnoreNullAge() {

            userDao.save(new User("Бобнулл>", "qwe@qwe.ru", null));
            userDao.save(new User("Боб22", "asd@asd.ru", 22));

            List<User> adults = userDao.findAllAdults();

            assertThat(adults).hasSize(1);
            assertThat(adults.get(0).getName()).isEqualTo("Боб22");

        }

    }

    @Nested
    @DisplayName("Обновление данных пользователя (update)")
    class UpdateTests {

        @Test
        @DisplayName("Обновление всех полей")
        void testUpdateAllFields() {

            User old = new User("Боб", "qwe@qwe.ru", 22);
            userDao.save(old);

            User changes = new User();
            changes.setName("НеБоб");
            changes.setEmail("poi@poi.ru");
            changes.setAge(33);

            userDao.update(old.getId(), changes);

            User updated = userDao.findById(old.getId()).orElseThrow();
            assertThat(updated.getName()).isEqualTo("НеБоб");
            assertThat(updated.getEmail()).isEqualTo("poi@poi.ru");
            assertThat(updated.getAge()).isEqualTo(33);

        }

        @Test
        @DisplayName("Частичное обновление")
        void testUpdatePartial() {
            User original = new User("Боб", "qwe@qwe.ru", 22);
            userDao.save(original);

            User changes = new User();
            changes.setName("НеБоб");

            userDao.update(original.getId(), changes);

            User updated = userDao.findById(original.getId()).orElseThrow();
            assertThat(updated.getName()).isEqualTo("НеБоб");
            assertThat(updated.getEmail()).isEqualTo("qwe@qwe.ru");
            assertThat(updated.getAge()).isEqualTo(22);
        }

        @Test
        @DisplayName("Исключение при попытке обновить несуществующего пользователя")
        void testUpdateUserNotExist() {

            User changes = new User();
            changes.setName("Некто");

            assertThatThrownBy(() -> userDao.update(UUID.randomUUID(), changes))
                    .isInstanceOf(ResourceNotFoundException.class);

        }

    }

    @Nested
    @DisplayName("Удаление пользователя (delete)")
    class DeleteTests {

        @Test
        @DisplayName("Удаление пользователя по ID")
        void testDeleteUser() {

            User user = new User("Боб", "qwe@qwe.ru", 44);
            userDao.save(user);

            userDao.deleteById(user.getId());

            assertThat(userDao.findById(user.getId())).isEmpty();

        }

        @Test
        @DisplayName("Исключение при попытке удаления несмуществующего пользователя")
        void testDeleteNotExistUser() {
            assertThatThrownBy(() -> userDao.deleteById(UUID.randomUUID()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

    }

}