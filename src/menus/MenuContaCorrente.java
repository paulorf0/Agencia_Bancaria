package menus;

import classes.ContaCorrente;
import enums.Canal;
import exceptions.SaldoException;
import outros.Logica;
import outros.MetodosDB;
import outros.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MenuContaCorrente {

    public static void exibirMenu(Scanner scanner, ContaCorrente contaCorrente, String meu_cpf, Canal canal) {
        boolean executando = true;

        while (executando) {
            System.out.println("\n=== Menu Conta Corrente ===");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Depositar");
            System.out.println("3. Sacar");
            System.out.println("4. Transferir");
            System.out.println("5. Efetuar pagamento");
            System.out.println("6. Consultar Transações");
            System.out.println("7. Consultar informacoes gerais");
            System.out.println("8. Desativar conta");
            System.out.println("9. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();
            List<Integer> tipo;
            String CPF;
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
                    Logica.logicaTransf(scanner, meu_cpf, canal, contaCorrente);

                    break;
                case "5":
                    System.out.print("Digite o valor para pagamento: ");
                    BigDecimal valorPagamento = new BigDecimal(scanner.nextLine());
                    System.out.print("Digite o CPF da conta destino: ");
                    CPF = scanner.nextLine();
                    tipo = MetodosDB.consultarTipoConta(CPF);
                    if (!tipo.contains(2)) {
                        System.out.println("O cliente não possuí uma conta salário.");
                        break;
                    }

                    try {
                        contaCorrente.efetuarPagamento(CPF, valorPagamento, canal);
                    } catch (SaldoException e) {
                        System.out.println(e.getMessage());
                    }

                    break;
                case "6":
                    Utils.limparConsole();
                    System.out.println("Transacoes da conta: " + contaCorrente.getNro_conta());
                    System.out.println("--------------------------------------");
                    System.out.println("\n" + contaCorrente.consultarHist());
                    System.out.println("--------------------------------------");
                    break;
                case "7":
                    System.out.println(contaCorrente.consultarInf());
                    break;
                case "8":
                    contaCorrente.setSituacao(0);
                    System.out.println("Sua conta foi desativada. Um gerente deve ser consultado para reativar.");
                    executando = false;
                    break;
                case "9":
                    System.out.println("Saindo...");
                    executando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

}
