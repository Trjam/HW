package ru.trjamus.hw2;

import ru.trjamus.hw2.Console.Menu;
import ru.trjamus.hw2.Dao.UserDao;
import ru.trjamus.hw2.Dao.UserInterface;
import ru.trjamus.hw2.Utils.HibernateUtils;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        UserInterface userDao = new UserDao(HibernateUtils.getSessionFactory());
        try (Menu menu = new Menu(userDao)) {
            menu.run();
        } finally {
            HibernateUtils.shutdown();
        }
    }

}
