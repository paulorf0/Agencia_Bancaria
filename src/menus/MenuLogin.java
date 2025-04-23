package menus;

import classes.Cliente;
import classes.Conta;
import classes.ContaCorrente;
import classes.ContaPoupanca;
import classes.ContaSalario;
import classes.MetodosDB;
import enums.Canal;
import outros.Utils;
import outros.ValidarCPF;

import java.util.Scanner;

public class MenuLogin {
    private static final String CAMINHO_ARQUIVO = "DB";
    private static Canal canal;

    public static void escolherCanal(Scanner scanner) {
        boolean identificar = true;

        while (identificar) {
            System.out.println("Por qual canal você está acessando sua conta\n");
            System.out.println("1. INTERNETBAKING\n2. CAIXA ELETRONICO\n3. CAIXA_FISICO");

            String escolha = scanner.nextLine();

            switch (escolha) {
                case "1":
                    MenuLogin.canal = Canal.INTERNETBAKING;
                    identificar = false;
                    break;
                case "2":
                    MenuLogin.canal = Canal.CAIXA_ELETRONICO;
                    identificar = false;
                    break;
                case "3":
                    MenuLogin.canal = Canal.CAIXA_FISICO;
                    identificar = false;
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }

        }
        Utils.limparConsole();
    }

    public static void exibirMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean autenticado = false;
        MetodosDB.dbNome = CAMINHO_ARQUIVO;

        escolherCanal(scanner);

        while (!autenticado) {
            Utils.limparConsole();

            System.out.println("\n=== Banco Digital ===");
            System.out.println("1. Login");
            System.out.println("2. Cadastrar novo cliente");
            System.out.println("3. Cadastrar novo funcionario");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    autenticado = fazerLogin(scanner);
                    break;
                case "2":
                    cadastrarCliente(scanner);
                    break;

                case "4":
                    System.out.println("Saindo...");
                    autenticado = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static boolean fazerLogin(Scanner scanner) {
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        if (MetodosDB.consultarExiste(cpf) == 0) {
            System.out.println("CPF ou senha incorreto. Tente novamente.");
            return false;
        }

        if (MetodosDB.consultarSenha(cpf).equals(senha)) {

            int tipoConta = MetodosDB.consultarTipoConta(cpf);

            if (tipoConta == 3) {
                MenuFuncionario.Menu(scanner, cpf);
            } else if (tipoConta == 4) {
                MenuGerente.Menu(scanner, cpf);
            } else {
                Conta conta = MetodosDB.consultarConta(cpf);
                if (tipoConta == 0) {
                    ContaCorrente corrente = (ContaCorrente) conta;
                    MenuContaCorrente.exibirMenu(scanner, corrente, MenuLogin.canal);

                    MetodosDB.salvar(corrente);
                } else if (tipoConta == 1) {
                    ContaPoupanca poupanca = (ContaPoupanca) conta;
                    MenuContaPoupanca.exibirMenu(scanner, poupanca, MenuLogin.canal);

                    MetodosDB.salvar(poupanca);
                } else if (tipoConta == 2) {
                    ContaSalario salario = (ContaSalario) conta;
                    MenuContaSalario.exibirMenu(scanner, salario, MenuLogin.canal);

                    MetodosDB.salvar(salario);
                } else {
                    System.out.println("Tipo de conta não reconhecido. Encerrando sessão.");
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private static void cadastrarCliente(Scanner scanner) {
        Utils.limparConsole();

        System.out.print("Digite o CPF: ");
        String cpf = scanner.nextLine();

        if (!ValidarCPF.validar(cpf)) {
            System.out.println("CPF inválido. Cadastro não realizado.");
            return;
        } else if (MetodosDB.consultarExiste(cpf) == 1) {
            System.out.println("CPF ja cadastrado no sistema.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        System.out.print("Tipo de conta (corrente = 0/poupanca = 1/salario = 2): ");
        int tipoConta;
        while (true)
            try {
                tipoConta = Integer.parseInt(scanner.nextLine());
                if (tipoConta < 0 || tipoConta > 2)
                    continue;
                break;
            } catch (NumberFormatException e) {
                System.out.println("\n Numero digitado invalido.");
            }

        int nro_agencia;
        System.out.print("Numero da agencia: ");
        while (true)
            try {
                nro_agencia = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("\n Numero digitado invalido.");
            }

        if (tipoConta == 0) {
            ContaCorrente corrente = new ContaCorrente(senha, nro_agencia);
            Cliente novo_cliente = new Cliente(cpf, nome, corrente);
            MetodosDB.salvar(novo_cliente);
        } else if (tipoConta == 1) {
            ContaPoupanca poupanca = new ContaPoupanca(senha, nro_agencia);
            Cliente novo_cliente = new Cliente(cpf, nome, poupanca);
            MetodosDB.salvar(novo_cliente);

        } else {
            ContaSalario salario = new ContaSalario(senha, nro_agencia);
            Cliente novo_cliente = new Cliente(cpf, nome, salario);
            MetodosDB.salvar(novo_cliente);
        }
    }
}
