package ru.trjamus.hw2.DaoAkaDto;

import ru.trjamus.hw2.Entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserInterface {

    void save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    void update(User user);

    void delete(User user);

    List<User> findAllAdults();

}
