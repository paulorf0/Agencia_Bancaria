package outros;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import classes.Conta;
import enums.Canal;
import exceptions.SaldoException;

public class Logica {

    public static boolean logicaTransf(Scanner scanner, String meu_cpf, Canal canal, Conta conta) {
        System.out.print("Digite o valor para transferência: ");

        BigDecimal valorTransferencia;
        try {
            valorTransferencia = new BigDecimal(scanner.nextLine());
            if (valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Valor da transferência deve ser positivo.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido para transferência.");
            return false;
        }

        System.out.print("Digite o CPF da conta destino: ");
        String cpfDestino = scanner.nextLine();

        List<Integer> tiposContaDestino = MetodosDB.consultarTipoConta(cpfDestino);

        if (tiposContaDestino == null || tiposContaDestino.isEmpty()) {
            System.out.println("CPF destino não cadastrado no sistema ou não possui contas associadas.");
            return false;
        }

        // Apenas conta Corrente ou Poupança pode receber transferência.
        // Conta salário apenas recebe pagamento.

        // Se o cliente tiver duas contas de mesmo tipo (ex: duas contas correntes),
        // então tipos eligiveis fica com o número repetido.
        List<Integer> tiposElegiveisDestino = new ArrayList<>();
        for (Integer tipoConta : tiposContaDestino) {
            if (tipoConta == 0 || tipoConta == 1) {
                tiposElegiveisDestino.add(tipoConta);
            }
        }
        int tipoFinalParaTransferencia = -1;
        String nro;
        UUID nro_conta_transf;

        // CONTA SALARIO NÃO PODE RECEBER TRANSFERENCIA. O SYSTEM.OUT APENAS RETORNA QUE
        // "CPF NAO CADASTRO" PARA QUANDO TENTA TRANSFERIR PARA CONTA SALARIO.

        // Se for para conta de mesmo CPF
        if (cpfDestino.equals(meu_cpf)) {
            // Transferência para o próprio CPF
            tiposElegiveisDestino.remove(Integer.valueOf(conta.getTipoConta()));

            if (tiposElegiveisDestino.isEmpty()) {
                System.out.println("Você não possuí outra conta válida para fazer a transferência.");
                return false;
            } else {
                List<String> nro_minha_conta = MetodosDB.consultarNroConta(cpfDestino);
                nro_minha_conta.remove(conta.getNro_conta().toString());

                nro = nro_minha_conta.getFirst();
            }

            tipoFinalParaTransferencia = tiposElegiveisDestino.get(0);
            System.out.println("A transferência será feita para a sua outra conta "
                    + (tipoFinalParaTransferencia == 0 ? "Corrente" : "Poupança") + ".");

        } else {
            // Transferência para outro CPF
            if (tiposElegiveisDestino.isEmpty()) {
                if (tiposContaDestino.contains(2)) // Se o destino só tem conta Salário
                    System.out.println(
                            "O cliente destino só possui conta Salário. Considere fazer um pagamento.");
                else
                    System.out.println(
                            "O cliente destino não possui contas do tipo Corrente ou Poupança para receber a transferência.");
                return false;
            } else if (tiposElegiveisDestino.size() == 1) {
                tipoFinalParaTransferencia = tiposElegiveisDestino.get(0);
                nro = MetodosDB.consultarNroConta(cpfDestino, tipoFinalParaTransferencia); // O cliente só
                                                                                           // possuí uma
                                                                                           // conta de tipo
                                                                                           // válido.

                System.out.println("A transferência será feita para a conta "
                        + (tipoFinalParaTransferencia == 0 ? "Corrente" : "Poupança")
                        + " do cliente destino.");
            } else { // Múltiplas contas elegíveis no CPF destino
                System.out.println(
                        "O cliente destino possui mais de uma conta válida para transferência.");
                System.out.println("Escolha para qual tipo de conta deseja transferir:");

                nro = Utils.nro_conta_transferencia(cpfDestino, scanner);
                if (nro == null) {
                    System.out.println("Erro interno. Encerrando.");
                    return false;
                }

                try {
                    nro_conta_transf = UUID.fromString(nro);
                } catch (Exception e) {
                    System.out.println("Algum erro interno ocorreu. Encerrando");
                    return false;
                }
                tipoFinalParaTransferencia = MetodosDB.consultarTipoConta(nro_conta_transf);

                System.out.println("Transferência será direcionada para a conta "
                        + (tipoFinalParaTransferencia == 0 ? "Corrente" : "Poupança")
                        + " do cliente destino.");
            }

        } // Se um tipo final foi determinado e é válido, prosseguir com a transferência

        if (nro == null) {
            System.out.println("Erro interno. Encerrando.");
            return false;
        }

        try {
            nro_conta_transf = UUID.fromString(nro);
        } catch (Exception e) {
            System.out.println("Algum erro interno ocorreu. Encerrando");
            return false;
        }

        if (tipoFinalParaTransferencia == 0 || tipoFinalParaTransferencia == 1) {
            try {
                conta.transferir(cpfDestino, nro_conta_transf, valorTransferencia, canal);
                System.out.println("Transferência no valor de " + valorTransferencia + " para o CPF "
                        + cpfDestino + " ("
                        + (tipoFinalParaTransferencia == 0 ? "Conta Corrente" : "Conta Poupança")
                        + ") processada.");
            } catch (SaldoException e) {
                System.out.println("Erro ao processar a transferência: " + e.getMessage());
                return false;
            } catch (Exception e) {
                // Captura outras exceções inesperadas durante a transferência
                System.out.println(
                        "Ocorreu um erro inesperado ao tentar realizar a transferência: " + e.getMessage());
                return false;
            }
        } else if (tipoFinalParaTransferencia != -1) {
            System.out.println("Tipo de conta destino inválido para transferência após seleção.");
            return false;
        }
        return true;

    }

}
