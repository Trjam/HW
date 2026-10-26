package ru.trjamus.hw2.Dao;

import ru.trjamus.hw2.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserInterface {

    void save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    void update(UUID id, User changes);

    void deleteById(UUID id);

    List<User> findAllAdults();

}
