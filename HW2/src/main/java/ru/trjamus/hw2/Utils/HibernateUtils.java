package ru.trjamus.hw2.Utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
public class HibernateUtils {

    //прям не нравится идеа без гетера на сатичный класс, ну ок
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtils() {}

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration();

            //напоминалка, живут в переменных среды идеа теперь
            // DB_URL=jdbc:postgresql://<урл>:<порт>/<название сервиса/базы>;DB_USER=<логин>;DB_PASSWORD=<пароль>

            String url  = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASSWORD");

            if (url  != null) cfg.setProperty("hibernate.connection.url", url);
            if (user != null) cfg.setProperty("hibernate.connection.username", user);
            if (pass != null) cfg.setProperty("hibernate.connection.password", pass);

            return cfg.configure().buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Ошибка инициализации SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("SessionFactory закрыт");
        }
    }

}
