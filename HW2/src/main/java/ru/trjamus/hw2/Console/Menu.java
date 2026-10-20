package ru.trjamus.hw2.Console;

import ru.trjamus.hw2.DaoAkaDto.UserDao;
import ru.trjamus.hw2.DaoAkaDto.UserInterface;
import ru.trjamus.hw2.Entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Menu implements AutoCloseable {
    private final UserInterface userDao;
    private final Scanner scanner;

    private void printMenu() {
        System.out.print("""
            
            --- Выбирай мудро ---
            1. Плюсануть нового юзера
            2. Найти юзера по айдишке
            3. Показать всех
            4. Тоже самое, но строго 18+
            5. Обновить юзера
            6. Минусануть юзера
            
            0. Схлопнуть приложение
            -------------------------------------
            
            Сюда циферку с меню выше:\s""");
    }

    public Menu() {
        this.userDao = new UserDao();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

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
                default -> System.out.println("Очепятка, попробуй еще разок");
            }
        }
    }

    private void createUser() {
        System.out.print("Сюда имя: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Ну введи хотяб 1 символ");
            return;
        }

        System.out.print("Сюда мыло: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Мыло не обнаружено");
            return;
        }

        Integer age = readInt("А сюда возраст: ");
        if (age == null) return;

        try {
            userDao.save(new User(name, email, age));
            System.out.println("Успешно создали юзера");
        } catch (Exception e) {
            System.err.println("Не создался юзер: " + e.getMessage());
        }
    }

    private void findUserById() {
        UUID id = readUuid("Сюда айдишку юзера (зря UUID сделал в этом проекте видать): ");
        if (id == null) return;

        userDao.findById(id).ifPresentOrElse(
                user -> beautyOut(List.of(user)),
                () -> System.out.println("Нет таких.")
        );
    }

    private void findAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Нет никого, все ушли на север.");
        } else {
            beautyOut(users);
        }
    }

    private void findAllAdultsUsers() {
        List<User> users = userDao.findAllAdults();
        if (users.isEmpty()) {
            System.out.println("Нет никого, все ушли на север.");
        } else {
            beautyOut(users);;
        }
    }

    private void updateUser() {
        UUID id = readUuid("Сюда айдишку юзера для правки: ");
        if (id == null) return;

        Optional<User> found = userDao.findById(id);
        if (found.isEmpty()) {
            System.out.println("Нет таких.");
            return;
        }

        User user = found.get();

        System.out.print("Новое имя (Enter — пропустить): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) user.setName(newName);

        System.out.print("Новое мыло (Enter — пропустить): ");
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.isEmpty()) user.setEmail(newEmail);

        System.out.print("Новый возраст (Enter — пропустить): ");
        String newAge = scanner.nextLine().trim();
        if (!newAge.isEmpty()) {
            try {
                user.setAge(Integer.parseInt(newAge));
            } catch (NumberFormatException e) {
                System.out.println("Число надо ввести, цыферки, не буковки.");
            }
        }

        try {
            userDao.update(user);
            System.out.println("Успешно обновили юзера c ID " + user.getId());
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении: " + e.getMessage());
        }
    }

    private void deleteUser() {
        UUID id = readUuid("Введи айди, кого дропнуть: ");
        if (id == null) return;

        userDao.findById(id).ifPresentOrElse(
                user -> {
                    userDao.delete(user);
                    System.out.println("Дропнули вот его: ");
                    beautyOut(List.of(user));
                },
                () -> System.out.println("А нет уже таких")
        );
    }

    private UUID readUuid(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            System.out.println("Это не UUID.");
            return null;
        }
    }

    private Integer readInt(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Целое число надобно.");
            return null;
        }
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


    @Override
    public void close() {
        scanner.close();
    }
}
