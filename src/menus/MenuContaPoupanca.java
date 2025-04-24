package menus;

import classes.ContaPoupanca;
import enums.Canal;
import exceptions.SaldoException;
import outros.Utils;

import java.math.BigDecimal;
import java.util.Scanner;

public class MenuContaPoupanca {

    public static void exibirMenu(Scanner scanner, ContaPoupanca contaPoupanca, Canal canal) {
        boolean executando = true;

        while (executando) {
            System.out.println("\n=== Menu Conta Poupança ===");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Depositar");
            System.out.println("3. Sacar");
            System.out.println("4. Transferir");
            System.out.println("5. Efetuar pagamento");
            System.out.println("6. Calcular rendimento");
            System.out.println("7. Aplicar rendimento");
            System.out.println("8. Consultar informacoes gerais");
            System.out.println("9. Consultar Transações");
            System.out.println("10. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    contaPoupanca.consultarSaldo();

                    break;
                case "2":
                    System.out.print("Digite o valor para depósito: ");
                    BigDecimal valorDeposito = new BigDecimal(scanner.nextLine());

                    try {
                        contaPoupanca.depositar(valorDeposito, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "3":
                    System.out.print("Digite o valor para saque: ");
                    BigDecimal valorSaque = new BigDecimal(scanner.nextLine());

                    try {
                        contaPoupanca.sacar(valorSaque, canal);
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
                        contaPoupanca.transferir(CPF, valorTransferencia, canal);
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
                        contaPoupanca.efetuarPagamento(CPF, valorPagamento, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "6":
                    contaPoupanca.calcularRendimento();
                    contaPoupanca.consultarSaldo();
                    break;
                case "7":
                    contaPoupanca.aplicarRendimento();
                    contaPoupanca.consultarSaldo();
                    break;
                case "8":
                    System.out.println(contaPoupanca.consultarInf());
                    break;
                case "9":
                    Utils.limparConsole();
                    System.out.println("Transacoes da conta: " + contaPoupanca.getNro_conta());
                    System.out.println("--------------------------------------");
                    System.out.println("\n" + contaPoupanca.consultarHist());
                    System.out.println("--------------------------------------");
                    break;
                case "10":
                    System.out.println("Saindo...");
                    executando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }

    }

}
