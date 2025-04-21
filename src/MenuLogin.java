package menus;

import classes.Cliente;
import classes.ClienteLoader;
import classes.Conta;
import classes.ContaCorrente;
import classes.ContaPoupanca;
import classes.ContaSalario;
import outros.ValidarCPF;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MenuLogin {
    private static final String CAMINHO_ARQUIVO = "cliente.txt";

    public static void exibirMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean autenticado = false;

        while (!autenticado) {
            System.out.println("\n=== Banco Digital ===");
            System.out.println("1. Login");
            System.out.println("2. Cadastrar novo cliente");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> {
                    autenticado = fazerLogin(scanner);
                }
                case "2" -> {
                    cadastrarCliente(scanner);
                }
                case "3" -> {
                    System.out.println("Saindo...");
                    return;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static boolean fazerLogin(Scanner scanner) {
        List<Cliente> clientes = ClienteLoader.lerClientesDeArquivo(CAMINHO_ARQUIVO);

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        for (Cliente cliente : clientes) {
            if (cliente.getCpf() != null && cliente.getCpf().equals(cpf)) {
                Conta conta = cliente.getConta();
                if (conta != null && conta.getSenha() != null && conta.getSenha().equals(senha)) {
                    String tipoConta = conta instanceof ContaCorrente ? "corrente"
                            : conta instanceof ContaPoupanca ? "poupança"
                            : conta instanceof ContaSalario ? "salário"
                            : "desconhecido";

                    System.out.println("Login efetuado com sucesso!");
                    System.out.println("Bem-vindo(a), " + cliente.getNome() + "! Sua conta é do tipo: " + tipoConta);

                    return true;
                }
            }
        }

        System.out.println("CPF ou senha incorretos. Tente novamente.");
        return false;
    }

    private static void cadastrarCliente(Scanner scanner) {
        List<Cliente> clientes = ClienteLoader.lerClientesDeArquivo(CAMINHO_ARQUIVO);

        System.out.print("Digite o CPF: ");
        String cpf = scanner.nextLine();

        if (!ValidarCPF.validar(cpf)) {
            System.out.println("CPF inválido. Cadastro não realizado.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("Saldo inicial: ");
        BigDecimal saldo = new BigDecimal(scanner.nextLine());
        LocalDateTime dataAbertura = LocalDateTime.now();

        System.out.print("Tipo de conta (corrente/poupanca/salario): ");
        String tipoConta = scanner.nextLine().toLowerCase();

        //gerar o numero da conta
        int maiorNumero = 100;
        for (Cliente cliente : clientes) {
            if (cliente.getConta().getNro_conta() > maiorNumero) {
                maiorNumero = cliente.getConta().getNro_conta();
            }
        }
        int nroContaGerado = maiorNumero + 1;

        String linha = String.join(",", cpf, nome, senha, String.valueOf(nroContaGerado),
                saldo.toString(), dataAbertura.toString(), tipoConta);

        try (FileWriter fw = new FileWriter(CAMINHO_ARQUIVO, true)) {
            fw.write(linha + "\n");
            System.out.println("Cliente cadastrado com sucesso!");
            System.out.println("Número da conta gerado: " + nroContaGerado);
        } catch (IOException e) {
            System.out.println("Erro ao salvar cliente: " + e.getMessage());
        }
    }
}
