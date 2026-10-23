package ru.trjamus.hw2.Utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
public class HiberUtils {

    //прям не нравится идеа без гетера на тсатичный класс, ну ок
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HiberUtils() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            log.error("SessionFactory не завелся", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("SessionFactory потух");
        }
    }

}
