package ru.trjamus.hw2.Dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import lombok.extern.slf4j.Slf4j;
import ru.trjamus.hw2.exception.DataAccessException;
import ru.trjamus.hw2.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class BaseDao<T, ID> {

    protected final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    protected BaseDao(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    protected Session session() {
        return sessionFactory.getCurrentSession();
    }

    // оборачиваем транзакции с возвратом и без

    protected void executeVoid(Consumer<Session> action) {
        Session s = session();
        Transaction tx = s.beginTransaction();
        try {
            action.accept(s);
            tx.commit();
        } catch (ResourceNotFoundException | DataAccessException e) {
            tx.rollback();
            throw e;
        } catch (RuntimeException e) {
            tx.rollback();
            log.error("Ошибка БД", e);
            throw new DataAccessException("Ошибка БД: " + e.getMessage(), e);
        }
    }

    protected <R> R execute(Function<Session, R> action) {
        Session s = session();
        Transaction tx = s.beginTransaction();
        try {
            R result = action.apply(s);
            tx.commit();
            return result;
        } catch (ResourceNotFoundException | DataAccessException e) {
            tx.rollback();
            throw e;
        } catch (RuntimeException e) {
            tx.rollback();
            log.error("Ошибка БД", e);
            throw new DataAccessException("Ошибка БД: " + e.getMessage(), e);
        }
    }

    // универсальный круд для всех сущностей

    public void save(T entity) {
        executeVoid(s -> s.persist(entity));
    }

    public Optional<T> findById(ID id) {
        return execute(s -> Optional.ofNullable(s.find(entityClass, id)));
    }

    public List<T> findAll() {
        return execute(s ->
                s.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList());
    }

    //поиск и удаление в одном флаконе
    public void deleteById(ID id) {
        executeVoid(s -> {
            T entity = s.find(entityClass, id);
            if (entity == null) {
                throw new ResourceNotFoundException(entityClass.getSimpleName(), id);
            }
            s.remove(entity);
        });
    }

    //поиск и апдейт в одном флаконе
    protected void modifyById(ID id, Consumer<T> modifier) {
        executeVoid(s -> {
            T entity = s.find(entityClass, id);
            if (entity == null) {
                throw new ResourceNotFoundException(entityClass.getSimpleName(), id);
            }
            modifier.accept(entity);
        });
    }
}
