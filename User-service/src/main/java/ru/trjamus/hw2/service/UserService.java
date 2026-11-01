package ru.trjamus.hw2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trjamus.hw2.Utils.Validator;
import ru.trjamus.hw2.dto.UserRequest;
import ru.trjamus.hw2.dto.UserResponse;
import ru.trjamus.hw2.dto.UserUpdateRequest;
import ru.trjamus.hw2.entity.User;
import ru.trjamus.hw2.exception.ResourceNotFoundException;
import ru.trjamus.hw2.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final Validator userValidator;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<UserResponse> findAllAdults() {
        return userRepository.findAllByAgeGreaterThanEqual(18).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse findById(UUID id) {
        return toResponse(getUserById(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        userValidator.validateEmailIsUnique(request.getEmail());

        User user = new User(
                request.getName().trim(),
                request.getEmail().trim(),
                request.getAge()
        );
        return toResponse(userRepository.saveAndFlush(user));
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = getUserById(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!user.getEmail().equals(request.getEmail())) {
                userValidator.validateEmailIsUnique(request.getEmail());
                user.setEmail(request.getEmail().trim());
            }
        }

        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteById(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    private User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getCreatedAt());
    }
}