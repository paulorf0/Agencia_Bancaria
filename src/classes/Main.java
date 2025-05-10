package classes;

import java.io.FileOutputStream;
import java.io.IOException;

import menus.MenuLogin;
import outros.MetodosDB;
import outros.Utils;

public class Main {
    public static void main(String[] args) {
        try (FileOutputStream _ = new FileOutputStream("DB", true)) {
        } catch (IOException e) {
        }
        MetodosDB.dbNome = "DB";
        Utils.limparConsole();
        MenuLogin.exibirMenu();
        System.out.println(MetodosDB.dados());

        // Cliente no Banco Corrente: 14762706000 e
        // Gerente: 33578108081
        // Cliente Poupanca: 87055560071 - senha 123 (Poupança), senha 12 (Corrente)
        // Cliente Salario: 12843140064 - senha 123
    }
}
