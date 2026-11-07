package ru.trjamus.hw2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.trjamus.hw2.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByAgeGreaterThanEqual_returnsOnlyAdults() {
        userRepository.save(new User("малой", "17@qqq.ru", 17));
        userRepository.save(new User("взрослый", "18@qqq.ru", 18));

        List<User> adults = userRepository.findAllByAgeGreaterThanEqual(18);

        assertThat(adults).hasSize(1);
        assertThat(adults.get(0).getName()).isEqualTo("взрослый");
    }

    @Test
    void existsByEmail_returnsTrueForExistingEmail() {
        userRepository.save(new User("Боб", "qwe@qwe.ru", 22));
        assertThat(userRepository.existsByEmail("qwe@qwe.ru")).isTrue();
        assertThat(userRepository.existsByEmail("zzzz@zzz.ru")).isFalse();
    }

    @Test
    void save_throwsDataIntegrityViolation_whenEmailIsDuplicate() {
        userRepository.saveAndFlush(new User("Боб", "qwe@qwe.ru", 22));

        assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.saveAndFlush(new User("НеБоб", "qwe@qwe.ru", 33)));
    }
}