package ru.trjamus.hw2.Dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.trjamus.hw2.entity.User;

@Testcontainers
public abstract class AbstractDaoIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    protected static SessionFactory sessionFactory;

    @BeforeAll
    static void setUpSessionFactory() {

        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        cfg.setProperty("hibernate.connection.url",      postgres.getJdbcUrl());
        cfg.setProperty("hibernate.connection.username", postgres.getUsername());
        cfg.setProperty("hibernate.connection.password", postgres.getPassword());

        cfg.setProperty("hibernate.current_session_context_class", "thread");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.show_sql", "false");

        cfg.addAnnotatedClass(User.class);

        sessionFactory = cfg.buildSessionFactory();

    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    protected static void truncateTables(String... tables) {

        if (tables.length == 0) return;

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeMutationQuery(
                    "TRUNCATE TABLE " + String.join(", ", tables) + " RESTART IDENTITY CASCADE"
            ).executeUpdate();
            tx.commit();
        }

    }
}