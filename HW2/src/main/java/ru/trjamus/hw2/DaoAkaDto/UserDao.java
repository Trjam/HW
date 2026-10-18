package ru.trjamus.hw2.DaoAkaDto;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.trjamus.hw2.Entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDao implements UserInterface {

    private Session session(){
        return null;
    }


    @Override
    public void save(User user) {

        Transaction transaction = session().beginTransaction();

        try {
            session().persist(user);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw  e;
        }

    }

    @Override
    public Optional<User> findById(UUID id) {

        Transaction transaction = session().beginTransaction();

        try {
            Optional<User> result = Optional.ofNullable(session().find(User.class,id));
            transaction.commit();
            return result;
        } catch (RuntimeException e){
            transaction.rollback();
            throw e;
        }

    }

    @Override
    public List<User> findAll() {

        Transaction transaction = session().beginTransaction();

        try {
            List<User> result = session().createQuery("FROM User", User.class).list();
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
        return List.of();
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(UUID id) {

    }
}
