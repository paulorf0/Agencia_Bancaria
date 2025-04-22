package menus;

import classes.ContaCorrente;
import enums.Canal;
import exceptions.SaldoException;

import java.math.BigDecimal;
import java.util.Scanner;

public class MenuContaCorrente {

    public static void exibirMenu(Scanner scanner, ContaCorrente contaCorrente, Canal canal) {
        boolean executando = true;

        while (executando) {
            System.out.println("\n=== Menu Conta Corrente ===");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Depositar");
            System.out.println("3. Sacar");
            System.out.println("4. Transferir");
            System.out.println("5. Efetuar pagamento");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            // TO-DO: Todos os métodos de manipulação retornam um tipo de exception. Deve
            // ser tratado.
            switch (opcao) {
                case "1":
                    contaCorrente.consultarSaldo();
                    break;
                case "2":
                    System.out.print("Digite o valor para depósito: ");
                    BigDecimal valorDeposito = new BigDecimal(scanner.nextLine());

                    try {
                        contaCorrente.depositar(valorDeposito, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "3":
                    System.out.print("Digite o valor para saque: ");
                    BigDecimal valorSaque = new BigDecimal(scanner.nextLine());

                    try {
                        contaCorrente.sacar(valorSaque, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "4":
                    System.out.print("Digite o valor para transferência: ");
                    BigDecimal valorTransferencia = new BigDecimal(scanner.nextLine());
                    System.out.print("Digite o CPF da conta destino: ");
                    String CPF = scanner.nextLine();

                    try {
                        contaCorrente.transferir(CPF, valorTransferencia, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "5":
                    System.out.print("Digite o valor para pagamento: ");
                    BigDecimal valorPagamento = new BigDecimal(scanner.nextLine());
                    System.out.print("Digite o CPF da conta destino: ");
                    CPF = scanner.nextLine();

                    try {
                        contaCorrente.efetuarPagamento(CPF, valorPagamento, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "6":
                    System.out.println("Saindo...");
                    executando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

}
