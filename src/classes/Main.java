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

        // NRO AGENCIA: 141 - CENTRO E 145 - MARTINS
        // Gerente: 33578108081 - senha 123
        // Funcionario: 02994841061 - senha 121
        // Cliente Conta Corrente: 14762706000 - senha 110
        // Cliente Poupanca: 87055560071 - senha 323 (Poupança), senha 120 (Corrente)
        // Cliente Salario: 12843140064 - senha 123

    }
}
