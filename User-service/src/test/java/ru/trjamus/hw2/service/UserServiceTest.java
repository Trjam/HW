package ru.trjamus.hw2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trjamus.hw2.Utils.Validator;
import ru.trjamus.hw2.dto.UserRequest;
import ru.trjamus.hw2.dto.UserResponse;
import ru.trjamus.hw2.dto.UserUpdateRequest;
import ru.trjamus.hw2.entity.User;
import ru.trjamus.hw2.exception.ResourceNotFoundException;
import ru.trjamus.hw2.exception.ValidationException;
import ru.trjamus.hw2.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator userValidator;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_returnsUserResponse_whenUserExists() {
        UUID id = UUID.randomUUID();
        User user = new User("Боб", "qwe@qwe.ru", 25);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponse response = userService.findById(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.name()).isEqualTo("Боб");
    }

    @Test
    void findById_throwsException_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesUser_whenEmailIsUnique() {
        UserRequest request = new UserRequest();
        request.setName("Тот");
        request.setEmail("asd@asd.ru");
        request.setAge(30);

        User savedUser = new User("Тот", "asd@asd.ru", 30);
        savedUser.setId(UUID.randomUUID());
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.create(request);

        assertThat(response.name()).isEqualTo("Тот");
        verify(userValidator).validateEmailIsUnique("asd@asd.ru");
    }

    @Test
    void create_throwsException_whenEmailIsNotUnique() {
        UserRequest request = new UserRequest();
        request.setEmail("zzz@zzz.ru");

        doThrow(new ValidationException("Email уже существует"))
                .when(userValidator).validateEmailIsUnique(request.getEmail());

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(ValidationException.class);
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void update_changesOnlyProvidedFields() {
        UUID id = UUID.randomUUID();
        User existingUser = new User("Было", "ccc@ccc.ru", 22);
        existingUser.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setAge(44);

        userService.update(id, request);

        assertThat(existingUser.getName()).isEqualTo("Было");
        assertThat(existingUser.getEmail()).isEqualTo("ccc@ccc.ru");
        assertThat(existingUser.getAge()).isEqualTo(44);
    }

    @Test
    void delete_deletesUser_whenExists() {
        UUID id = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteById(id);

        verify(userRepository).delete(user);
    }
}