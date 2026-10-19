package ru.trjamus.hw2.DaoAkaDto;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.trjamus.hw2.Entity.User;
import ru.trjamus.hw2.Utils.HiberUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("resource")
public class UserDao implements UserInterface {

    private Session session(){
        return HiberUtils.getSessionFactory().getCurrentSession();
    }

    @Override
    public void save(User user) {

        Session session = session();
        Transaction transaction = session.beginTransaction();

        try {
            session.persist(user);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw  e;
        }

    }

    @Override
    public Optional<User> findById(UUID id) {

        Session session = session();
        Transaction transaction = session.beginTransaction();

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

        Session session = session();
        Transaction transaction = session.beginTransaction();

        try {
            List<User> result = session().createQuery("FROM User", User.class).list();
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void update(User user) {

        Session session = session();
        Transaction transaction = session.beginTransaction();

        try {
            session.merge(user);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

    }

    @Override
    public void delete(User user) {

        Session session = session();
        Transaction transaction = session.beginTransaction();

        try {
            session.remove(user);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }

    }

    //что то понял что нативный то не попробовал, ну пущай кривенкий будет

    @Override
    public List<User> findAllNotKids() {

        Session session = session();
        Transaction transaction = session.beginTransaction();

        try {
            List<User> result = session()
                    .createNativeQuery("Select * from users where age >= 18", User.class)
                    .list();
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }
}
