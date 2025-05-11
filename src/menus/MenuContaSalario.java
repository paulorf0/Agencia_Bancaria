package menus;

import classes.ContaSalario;
import enums.Canal;
import exceptions.SaldoException;
import outros.Logica;
import outros.MetodosDB;
import outros.Utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class MenuContaSalario {

    public static void exibirMenu(Scanner scanner, ContaSalario contaSalario, String meu_cpf, Canal canal) {
        boolean executando = true;

        while (executando) {
            System.out.println("\n=== Menu Conta Salário ===");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Depositar");
            System.out.println("3. Sacar");
            System.out.println("4. Transferir");
            System.out.println("5. Consultar informacoes gerais");
            System.out.println("6. Consultar Transações");
            System.out.println("7. Desativar conta");
            System.out.println("8. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();
            List<Integer> tipo;
            String CPF;
            switch (opcao) {
                case "1":
                    contaSalario.consultarSaldo();
                    break;
                case "2":
                    System.out.print("Digite o valor para depósito: ");
                    BigDecimal valorDeposito = new BigDecimal(scanner.nextLine());

                    try {
                        contaSalario.depositar(valorDeposito, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "3":
                    System.out.print("Digite o valor para saque: ");
                    BigDecimal valorSaque = new BigDecimal(scanner.nextLine());

                    try {
                        contaSalario.sacar(valorSaque, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "4":
                    Logica.logicaTransf(scanner, meu_cpf, canal, contaSalario);
                    break;
                case "5":
                    System.out.println(contaSalario.consultarInf());
                    break;
                case "6":
                    Utils.limparConsole();
                    System.out.println("Transacoes da conta: " + contaSalario.getNro_conta());
                    System.out.println("--------------------------------------");
                    System.out.println("\n" + contaSalario.consultarHist());
                    System.out.println("--------------------------------------");
                    break;
                case "7":
                    contaSalario.setSituacao(0);
                    System.out.println("Sua conta foi desativada. Um gerente deve ser consultado para reativar.");
                    executando = false;
                    break;
                case "8":
                    System.out.println("Saindo...");
                    executando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

}
