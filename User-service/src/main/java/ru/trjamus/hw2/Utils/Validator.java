package ru.trjamus.hw2.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trjamus.hw2.exception.ValidationException;
import ru.trjamus.hw2.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class Validator {

    private final UserRepository userRepository;

    public void validateEmailIsUnique(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("Email уже существует: " + email);
        }

    }

}

