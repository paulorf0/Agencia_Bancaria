package menus;

import java.util.Scanner;

import classes.Funcionario;
import outros.MetodosDB;
import outros.Utils;

public class MenuFuncionario {
    public static void Menu(Scanner scanner, String CPF) {
        boolean identificar = true;

        while (identificar) {
            System.out.println("1. Consultar minhas informacoes\n2. Sair");
            String escolha = scanner.nextLine();

            Funcionario func = MetodosDB.consultarFuncionario(CPF);
            
            switch (escolha) {
                case "1":
                    System.out.println("Numero da carteira: " + func.getNro_cart());
                    System.out.println("Salario: " + func.CalcSalario());
                    System.out.println("Cargo: " + func.getCargo());
                    System.out.println("Ano de Ingresso: " + func.getAnoIngresso().toString());
                    break;
                case "2":
                    Utils.limparConsole();
                    System.out.println("Saindo...");
                    identificar = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }

        }

        Utils.limparConsole();
    }
}
