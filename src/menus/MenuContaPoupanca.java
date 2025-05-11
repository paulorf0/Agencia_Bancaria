package menus;

import classes.ContaPoupanca;
import enums.Canal;
import exceptions.SaldoException;
import outros.MetodosDB;
import outros.Utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class MenuContaPoupanca {

    public static void exibirMenu(Scanner scanner, ContaPoupanca contaPoupanca, String meu_cpf, Canal canal) {
        boolean executando = true;

        while (executando) {
            System.out.println("\n=== Menu Conta Poupança ===");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Depositar");
            System.out.println("3. Sacar");
            System.out.println("4. Transferir");
            System.out.println("5. Calcular rendimento");
            System.out.println("6. Aplicar rendimento");
            System.out.println("7. Consultar informacoes gerais");
            System.out.println("8. Consultar Transações");
            System.out.println("9. Desativar Conta");
            System.out.println("10. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            List<Integer> tipo;
            String CPF;

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
                    CPF = scanner.nextLine();

                    if (CPF.equals(meu_cpf)) {
                        System.out.println("Não é possível fazer transferência para si mesmo.");
                        break;
                    }

                    tipo = MetodosDB.consultarTipoConta(CPF);
                    if (CPF.equals(meu_cpf)) {
                        if (tipo.size() == 1) {
                            System.out.println("Não é possível transferir.");
                            break;
                        }

                        tipo.remove(contaPoupanca.getTipoConta());
                        if (tipo.getFirst() == 0)
                            System.out.println("A transferência será feita para a sua conta corrente.");
                        else {
                            System.out.println("Sua outra conta é salário. Efetue um pagamento.");
                            break;
                        }

                        try {
                            contaPoupanca.transferir(CPF, tipo.getFirst(), valorTransferencia, canal);
                        } catch (SaldoException e) {
                            System.out.println(e.getMessage());
                        }

                    } else if (tipo.size() == 1 && (tipo.getFirst() == 0 || tipo.getFirst() == 1)) {
                        if (tipo.getFirst() == 1)
                            System.out.println("A transferência será feita para a conta poupança do cliente.");
                        else
                            System.out.println("A transferência será feita para a conta corrente do cliente.");

                        try {
                            contaPoupanca.transferir(CPF, tipo.getFirst(), valorTransferencia, canal);
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
                            contaPoupanca.transferir(CPF, tipoEscolhido, valorTransferencia, canal);
                        } catch (SaldoException e) {
                            System.out.println(e.getMessage());
                        }
                    } else
                        System.out.println("CPF não cadastrado no sistema.");
                    break;

                case "5":
                    contaPoupanca.calcularRendimento();
                    contaPoupanca.consultarSaldo();
                    break;
                case "6":
                    contaPoupanca.aplicarRendimento();
                    contaPoupanca.consultarSaldo();
                    break;
                case "7":
                    System.out.println(contaPoupanca.consultarInf());
                    break;
                case "8":
                    Utils.limparConsole();
                    System.out.println("Transacoes da conta: " + contaPoupanca.getNro_conta());
                    System.out.println("--------------------------------------");
                    System.out.println("\n" + contaPoupanca.consultarHist());
                    System.out.println("--------------------------------------");
                    break;
                case "9":
                    contaPoupanca.setSituacao(0);
                    System.out.println("Sua conta foi desativada. Um gerente deve ser consultado para reativar.");
                    executando = false;
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
