package menus;

import classes.Cliente;
import classes.ContaPoupanca;
import classes.Conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MenuContaPoupanca {

    public static void exibirMenu(Scanner scanner, Cliente cliente, ContaPoupanca contaPoupanca) {
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
            System.out.println("8. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    contaPoupanca.consultarSaldo("Saldo Poupança");
                    break;
                case "2":
                    System.out.print("Digite o valor para depósito: ");
                    BigDecimal valorDeposito = new BigDecimal(scanner.nextLine());
                    contaPoupanca.depositar(valorDeposito);
                    break;
                case "3":
                    System.out.print("Digite o valor para saque: ");
                    BigDecimal valorSaque = new BigDecimal(scanner.nextLine());
                    contaPoupanca.sacar(valorSaque);
                    break;
                case "4":
                    System.out.print("Digite o valor para transferência: ");
                    BigDecimal valorTransferencia = new BigDecimal(scanner.nextLine());
                    // Suponha que você tenha um método para buscar a conta pelo número
                    System.out.print("Digite o número da conta destino: ");
                    int nroContaDestino = Integer.parseInt(scanner.nextLine());
                    Conta contaDestino = buscarContaPorNumero(nroContaDestino);
                    contaPoupanca.transferir(contaDestino, valorTransferencia);
                    break;
                case "5":
                    System.out.print("Digite o valor para pagamento: ");
                    BigDecimal valorPagamento = new BigDecimal(scanner.nextLine());
                    contaPoupanca.efetuarPagamento(valorPagamento);
                    break;
                case "6":
                    contaPoupanca.calcularRendimento();
                    break;
                case "7":
                    contaPoupanca.aplicarRendimento();
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


    private static Conta buscarContaPorNumero(int nroConta) {
        return new ContaPoupanca("senha", nroConta, new BigDecimal(1000), LocalDateTime.now());
    }
}
