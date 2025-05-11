package menus;

import classes.ContaSalario;
import enums.Canal;
import exceptions.SaldoException;
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
            System.out.println("7. Sair");
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
                    System.out.print("Digite o valor para transferência: ");
                    BigDecimal valorTransferencia = new BigDecimal(scanner.nextLine());

                    System.out.print("Digite o CPF da conta destino: ");
                    CPF = scanner.nextLine();

                    tipo = MetodosDB.consultarTipoConta(CPF);
                    if (CPF.equals(meu_cpf)) {
                        if (tipo.size() == 1) {
                            System.err.println("Não é possível transferir.");
                            break;
                        }

                        tipo.remove(contaSalario.getTipoConta());
                        if (tipo.getFirst() == 0)
                            System.err.println("A transferência será feita para a sua conta corrente.");
                        else
                            System.err.println("A transferência será feita para a sua conta poupança.");

                        try {
                            contaSalario.transferir(CPF, tipo.getFirst(), valorTransferencia, canal);
                        } catch (SaldoException e) {
                            System.out.println(e.getMessage());
                        }

                    } else if (tipo.size() == 1 && (tipo.getFirst() == 0 || tipo.getFirst() == 1)) {
                        if (tipo.getFirst() == 1)
                            System.err.println("A transferência será feita para a conta poupança do cliente.");
                        else
                            System.err.println("A transferência será feita para a conta corrente do cliente.");

                        try {
                            contaSalario.transferir(CPF, tipo.getFirst(), valorTransferencia, canal);
                        } catch (SaldoException e) {
                            System.out.println(e.getMessage());
                        }

                    } else if (tipo.size() > 1 && (tipo.contains(0) || tipo.contains(1))) {
                        System.out.println("O cliente possui duas contas em seu CPF.");
                        System.out.println("Escolha para qual conta deseja transferir:");
                        int tipoEscolhido = Utils.capturar_tipo_transf(tipo, scanner);
                        if (tipoEscolhido == -1) {
                            System.out.println("Houve um erro interno.");
                            break;
                        }

                        try {
                            contaSalario.transferir(CPF, tipoEscolhido, valorTransferencia, canal);
                        } catch (SaldoException e) {
                            System.out.println(e.getMessage());
                        }
                    } else
                        System.err.println("CPF não cadastrado no sistema.");
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
                    System.out.println("Saindo...");
                    executando = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

}
