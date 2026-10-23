package ru.trjamus.hw2;

import ru.trjamus.hw2.Console.Menu;
import ru.trjamus.hw2.Utils.HiberUtils;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        try (Menu ui = new Menu()) {
            ui.run();
        } finally {
            HiberUtils.shutdown();
        }
    }

}