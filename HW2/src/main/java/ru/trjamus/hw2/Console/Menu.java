package ru.trjamus.hw2.Console;

import ru.trjamus.hw2.Dao.UserInterface;
import ru.trjamus.hw2.entity.User;
import ru.trjamus.hw2.Utils.Validator;
import ru.trjamus.hw2.exception.DataAccessException;
import ru.trjamus.hw2.exception.ResourceNotFoundException;
import ru.trjamus.hw2.exception.ValidationException;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Menu implements AutoCloseable {
    private final UserInterface userDao;
    private final Scanner scanner;

    public Menu(UserInterface userDao) {
        this(userDao, new Scanner(System.in));
    }

    public Menu(UserInterface userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    private void printMenu() {
        System.out.print("""
            
            --- Выбирай мудро ---
            1. Добавить пользователя
            2. Найти пользователя по ID
            3. Найти всех пользователей
            4. Найти всех взрослых пользователей (18+)
            5. Обновить данные пользователя
            6. Удалить пользователя по ID
            
            0. Закрыть приложение
            -------------------------------------
            
            Введите номер пункта меню:\s""");
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> findUserById();
                    case "3" -> findAllUsers();
                    case "4" -> findAllAdultsUsers();
                    case "5" -> updateUser();
                    case "6" -> deleteUser();
                    case "0" -> {
                        System.out.println("Добби свободен!");
                        return;
                    }
                    default -> System.out.println("Неверный ввод");
                }
            } catch (ValidationException | ResourceNotFoundException | DataAccessException e) {
                System.err.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private Integer readIntOrNull(String prompt) {
        String s = read(prompt);
        if (s.isBlank()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ValidationException("Ожидалось число");
        }
    }

    private UUID readUuid(String prompt) {
        String s = read(prompt);
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректный UUID.");
            return null;
        }
    }

    private void createUser() {
        System.out.println("Введите данные пользователя");
        String name = Validator.validateName(read("Имя: "));
        String email = Validator.validateEmail(read("Email: "));
        Integer age = Validator.validateAge(readIntOrNull("Возраст (Enter — пропустить): "));

        userDao.save(new User(name, email, age));
        System.out.println("Пользователь создан.");
        pause();
    }

    private void findUserById() {
        UUID id = readUuid("Введите ID пользователя для поиска: ");
        if (id == null) return;

        userDao.findById(id).ifPresentOrElse(
                user -> beautyOut(List.of(user)),
                () -> System.out.println("Пользователь не найден")
        );
        pause();
    }

    private void findAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены");
        } else {
            beautyOut(users);
        }
        pause();
    }

    private void findAllAdultsUsers() {
        List<User> users = userDao.findAllAdults();
        if (users.isEmpty()) {
            System.out.println("Взрослых не найдено");
        } else {
            beautyOut(users);
        }
        pause();
    }

    private void updateUser() {
        UUID id = readUuid("Введите ID пользователя для обновления: ");
        if (id == null) return;

        String name = read("Новое имя (Enter — пропустить): ");
        String email = read("Новый email (Enter — пропустить): ");
        Integer age = readIntOrNull("Новый возраст (Enter — пропустить): ");

        User updatedUserData = new User();
        if (!name.isBlank())  updatedUserData.setName(Validator.validateName(name));
        if (!email.isBlank()) updatedUserData.setEmail(Validator.validateEmail(email));
        if (age != null)      updatedUserData.setAge(Validator.validateAge(age));

        userDao.update(id, updatedUserData);
        System.out.println("Пользователь обновлён.");
        pause();
    }

    private void deleteUser() {
        UUID id = readUuid("Введите ID пользователя для удаления: ");
        if (id == null) return;

        userDao.deleteById(id);
        System.out.println("Пользователь удалён.");
        pause();
    }

    //подсмотрел тут красоту
    private void beautyOut(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("Список пуст.");
            return;
        }

        System.out.printf("%-36s | %-15s | %-25s | %-8s | %s%n",
                "ID", "Имя", "Email", "Возраст", "Создан");
        System.out.println("-".repeat(120));

        for (User u : users) {
            System.out.printf("%-36s | %-15s | %-25s | %-8s | %s%n",
                    u.getId(),
                    u.getName(),
                    u.getEmail(),
                    u.getAge() == null ? "—" : u.getAge(),
                    u.getCreatedAt());
        }
    }

    private void pause() {
        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
