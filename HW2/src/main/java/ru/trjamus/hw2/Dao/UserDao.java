package ru.trjamus.hw2.Dao;

import org.hibernate.SessionFactory;
import ru.trjamus.hw2.entity.User;

import java.util.List;
import java.util.UUID;

public class UserDao extends BaseDao<User, UUID> implements UserInterface {

    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    @Override
    public List<User> findAllAdults() {
        return execute(s ->
                s.createQuery("FROM User WHERE age >= 18", User.class).getResultList());
    }

    @Override
    public void update(UUID id, User changes) {
        modifyById(id, user -> {
            if (changes.getName() != null)  user.setName(changes.getName().trim());
            if (changes.getEmail() != null) user.setEmail(changes.getEmail().trim());
            if (changes.getAge() != null)   user.setAge(changes.getAge());
        });
    }
}