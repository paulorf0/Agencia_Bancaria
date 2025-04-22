package classes;

import java.io.FileOutputStream;
import java.io.IOException;

import menus.MenuLogin;

public class Main {
    public static void main(String[] args) {
        try (FileOutputStream _ = new FileOutputStream("DB", true)) {
        } catch (IOException e) {
        }
        MenuLogin.exibirMenu();
    }
}
