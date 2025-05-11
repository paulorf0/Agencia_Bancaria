package menus;

import classes.ContaCorrente;
import enums.Canal;
import exceptions.SaldoException;
import outros.MetodosDB;
import outros.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
                    System.out.print("Digite o valor para transferência: ");

                    BigDecimal valorTransferencia;
                    try {
                        valorTransferencia = new BigDecimal(scanner.nextLine());
                        if (valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("Valor da transferência deve ser positivo.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Valor inválido para transferência.");
                        break;
                    }

                    System.out.print("Digite o CPF da conta destino: ");
                    String cpfDestino = scanner.nextLine();

                    List<Integer> tiposContaDestino = MetodosDB.consultarTipoConta(cpfDestino);

                    if (tiposContaDestino == null || tiposContaDestino.isEmpty()) {
                        System.out.println("CPF destino não cadastrado no sistema ou não possui contas associadas.");
                        break;
                    }

                    // Apenas conta Corrente ou Poupança pode receber transferência.
                    // Conta salário apenas recebe pagamento.
                    List<Integer> tiposElegiveisDestino = new ArrayList<>();
                    for (Integer tipoConta : tiposContaDestino) {
                        if (tipoConta == 0 || tipoConta == 1) {
                            tiposElegiveisDestino.add(tipoConta);
                        }
                    }
                    int tipoFinalParaTransferencia = -1;

                    // CONTA SALARIO NÃO PODE RECEBER TRANSFERENCIA. O SYSTEM.OUT APENAS RETORNA QUE
                    // "CPF NAO CADASTRO" PARA QUANDO TENTA TRANSFERIR PARA CONTA SALARIO.

                    // Se for para conta de mesmo CPF
                    if (cpfDestino.equals(meu_cpf)) {
                        // Transferência para o próprio CPF
                        // Remove o tipo da conta de origem da lista de elegíveis

                        tiposElegiveisDestino.remove(Integer.valueOf(contaCorrente.getTipoConta()));
                        if (tiposElegiveisDestino.isEmpty()) {
                            System.out.println(
                                    "Não há outra conta elegível (Corrente/Poupança) em seu CPF para realizar a transferência.");
                            break;
                        }

                        tipoFinalParaTransferencia = tiposElegiveisDestino.get(0);
                        System.out.println("A transferência será feita para a sua outra conta "
                                + (tipoFinalParaTransferencia == 0 ? "Corrente" : "Poupança") + ".");
                                
                        // Conta destino é poupança ou corrente.
                    } else if (tipo.size() == 1 && (tipo.getFirst() == 0 || tipo.getFirst() == 1)) {
                        if (tipo.getFirst() == 1)
                            System.out.println("A transferência será feita para a conta poupança do cliente.");
                        else
                            System.out.println("A transferência será feita para a conta corrente do cliente.");

                        try {
                            contaCorrente.transferir(CPF, tipo.getFirst(), valorTransferencia, canal);
                        } catch (SaldoException e) {
                            System.out.println(e.getMessage());
                        }
                        // Se tiver mais de duas contas e tiver uma conta poupança ou corrente.
                    } else if (tipo.size() > 1 && (tipo.contains(0) || tipo.contains(1))) {
                        System.out.println("O cliente possui duas contas.");
                        System.out.println("Escolha para qual conta deseja transferir:");
                        int tipoEscolhido = Utils.capturar_tipo_transf(tipo, scanner);
                        if (tipoEscolhido == -1) {
                            System.out.println("Houve um erro interno.");
                            break;
                        }

                        try {
                            contaCorrente.transferir(CPF, tipoEscolhido, valorTransferencia, canal);
                        } catch (SaldoException e) {
                            System.out.println(e.getMessage());
                        }
                    } else if (tipo.contains(2)) {
                        System.out.println("O cliente só possuí conta salário. Faça um pagamento.");
                    } else
                        System.out.println("CPF não cadastrado no sistema.");
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
