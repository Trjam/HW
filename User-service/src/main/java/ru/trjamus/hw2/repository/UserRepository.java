package ru.trjamus.hw2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trjamus.hw2.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByAgeGreaterThanEqual(Integer age);
    boolean existsByEmail(String email);
}
