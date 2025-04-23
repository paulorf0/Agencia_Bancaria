package classes;

import java.io.FileOutputStream;
import java.io.IOException;

import menus.MenuLogin;
import outros.Utils;

public class Main {
    public static void main(String[] args) {
        try (FileOutputStream _ = new FileOutputStream("DB", true)) {
        } catch (IOException e) {
        }
        MetodosDB.dbNome = "DB";
        // Utils.limparConsole();
        System.out.println(MetodosDB.dados());
        // MenuLogin.exibirMenu();

        // Cliente no Banco: 14762706000 e 33578108081
    }
}
