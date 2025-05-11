package menus;

import classes.Agencia;
import classes.Cliente;
import classes.Conta;
import classes.ContaCorrente;
import classes.ContaPoupanca;
import classes.ContaSalario;
import classes.Endereco;
import classes.Funcionario;
import classes.Gerente;
import enums.Canal;
import enums.EstadoCivil;
import enums.Sexo;
import outros.MetodosDB;
import outros.Utils;
import outros.ValidarCPF;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

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
        boolean sair = false;
        MetodosDB.dbNome = CAMINHO_ARQUIVO;

        while (!sair) {

            System.out.println("\n=== Banco Digital ===");
            System.out.println("1. Login");
            System.out.println("2. Cadastrar novo gerente");
            System.out.println("3. Cadastrar agencia");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    fazerLogin(scanner);
                    break;
                case "2":
                    MenuCadastro.cadastrarGerente(scanner);
                    break;
                case "3":
                    MenuCadastro.cadastrarAgencia(scanner);
                    break;
                case "4":
                    System.out.println("Saindo...");
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void fazerLogin(Scanner scanner) {
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        if (MetodosDB.consultarExiste(cpf) == 0) {
            System.out.println("CPF ou senha incorreto. Tente novamente.");
            return;
        }
        List<String> senhas = MetodosDB.consultarSenha(cpf);
        if (MetodosDB.consultarExiste(cpf) == 1)
            if (senhas.getFirst().equals(senha)) {

                List<Integer> tipo = MetodosDB.consultarTipoConta(cpf);

                if (tipo.getFirst() == 3) {
                    MenuFuncionario.Menu(scanner, cpf);
                } else if (tipo.getFirst() == 4) {
                    MenuGerente.Menu(scanner, cpf);
                } else {
                    Conta conta = MetodosDB.consultarConta(cpf);

                    if (conta.getSituacao() == 0) {
                        System.out.println("Sua conta está inativa. É necessário que um gerente ative.");
                        return;
                    }

                    escolherCanal(scanner);

                    if (tipo.getFirst() == 0) {
                        ContaCorrente corrente = (ContaCorrente) conta;
                        MenuContaCorrente.exibirMenu(scanner, corrente, cpf, MenuLogin.canal);

                        MetodosDB.salvar(corrente);
                    } else if (tipo.getFirst() == 1) {
                        ContaPoupanca poupanca = (ContaPoupanca) conta;
                        MenuContaPoupanca.exibirMenu(scanner, poupanca, cpf, MenuLogin.canal);

                        MetodosDB.salvar(poupanca);
                    } else if (tipo.getFirst() == 2) {
                        ContaSalario salario = (ContaSalario) conta;
                        MenuContaSalario.exibirMenu(scanner, salario, cpf, MenuLogin.canal);

                        MetodosDB.salvar(salario);
                    } else {
                        System.out.println("Tipo de conta não reconhecido. Encerrando sessão.");
                        return;
                    }
                }

                return;
            } else {
                Utils.limparConsole();
                System.out.println("Senha ou CPF incorreto.");
                return;
            }
        else {
            // O cliente tem duas contas cadastradas de senhas diferentes.
            if (!senhas.getFirst().equals(senhas.getLast())) {
                // Se a senha digitada corresponde a alguma conta, então vai buscar a conta que
                // tem a mesma senha.
                if (senhas.getFirst().equals(senha) || senhas.getLast().equals(senha)) {

                    // Como um "cliente" ou é gerente ou é funcionário e como consultarConta retorna
                    // null caso seja um gerente ou funcionário, então, se não não conter o 3 (é
                    // funcionário),
                    // necessáriamente, foi tentado acessar uma conta de gerente.
                    Conta conta = MetodosDB.consultarConta(cpf, senha);
                    if (conta == null) {
                        List<Integer> tipo = MetodosDB.consultarTipoConta(cpf);

                        if (tipo.contains(3)) {
                            MenuFuncionario.Menu(scanner, cpf);
                        } else
                            MenuGerente.Menu(scanner, cpf);
                        return;
                    }

                    int tipoConta = conta.getTipoConta();

                    if (conta.getSituacao() == 0) {
                        System.out.println("Sua conta está inativa. É necessário que um gerente ative.");
                        return;
                    }
                    escolherCanal(scanner);

                    if (tipoConta == 0) {
                        ContaCorrente corrente = (ContaCorrente) conta;
                        MenuContaCorrente.exibirMenu(scanner, corrente, cpf, MenuLogin.canal);

                        MetodosDB.salvar(corrente);
                    } else if (tipoConta == 1) {
                        ContaPoupanca poupanca = (ContaPoupanca) conta;
                        MenuContaPoupanca.exibirMenu(scanner, poupanca, cpf, MenuLogin.canal);

                        MetodosDB.salvar(poupanca);
                    } else if (tipoConta == 2) {
                        ContaSalario salario = (ContaSalario) conta;
                        MenuContaSalario.exibirMenu(scanner, salario, cpf, MenuLogin.canal);

                        MetodosDB.salvar(salario);
                    } else {
                        System.out.println("Tipo de conta não reconhecido. Encerrando sessão.");
                        return;
                    }
                } else {
                    Utils.limparConsole();
                    System.out.println("Senha ou CPF incorreto.");
                    return;
                }
                return;
            } else { // Caso em que as senhas das contas são iguais
                if (!senhas.getFirst().equals(senha)) {
                    Utils.limparConsole();
                    System.out.println("Senha ou CPF invalido!");
                    return;
                }

                Utils.limparConsole();
                System.out.println("Duas contas cadastradas!\n");

                List<Integer> tipos = MetodosDB.consultarTipoConta(cpf);
                List<String> nros = MetodosDB.consultarNroConta(cpf);
                boolean eFuncionario = tipos.contains(3) || tipos.contains(4);
                int funcionarioEscolha = Utils.info_contas_login(cpf, tipos, nros); // Fazer print da seleção de contas
                                                                                    // para login.

                System.out.print("Escolha qual conta acessar: ");
                String opcao = scanner.nextLine();
                int index;
                try {
                    index = Integer.parseInt(opcao);
                    if (index < 1 || index > 2) {
                        System.out.println("Numero inválido");
                        return;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Numero inválido");
                    return;
                }

                if (eFuncionario && index == 2) {
                    // Usuário escolheu a opção 2, que é a conta de Funcionário ou Gerente
                    if (funcionarioEscolha == 3) {
                        MenuFuncionario.Menu(scanner, cpf);
                    } else if (funcionarioEscolha == 4) {
                        MenuGerente.Menu(scanner, cpf);
                    } else {
                        // Este caso não deveria ocorrer se funcionarioEscolha foi definido corretamente
                        System.out.println("Erro interno ao identificar tipo de Funcionário. Encerrando sessão.");
                    }
                    return;
                } else {
                    // Usuário escolheu uma conta regular (opção 1 no cenário misto, ou qualquer
                    // opção no cenário de duas contas regulares)
                    String numeroContaSelecionadaStr;
                    if (eFuncionario) {
                        if (tipos.get(0) == 3 || tipos.get(0) == 4)
                            numeroContaSelecionadaStr = nros.get(1);
                        else {
                            numeroContaSelecionadaStr = nros.get(0);
                        }
                    } else {
                        numeroContaSelecionadaStr = nros.get(index - 1);
                    }

                    UUID nro = UUID.fromString(numeroContaSelecionadaStr);
                    Conta conta = MetodosDB.consultarConta(cpf, nro);

                    if (conta == null) {
                        System.out.println("Erro ao carregar dados da conta selecionada. Encerrando sessão.");
                        return;
                    }

                    if (conta.getSituacao() == 0) {
                        System.out.println("Sua conta está inativa. É necessário que um gerente ative.");
                        return;
                    }

                    escolherCanal(scanner);
                    int tipoConta = conta.getTipoConta();

                    if (tipoConta == 0) {
                        ContaCorrente corrente = (ContaCorrente) conta;
                        MenuContaCorrente.exibirMenu(scanner, corrente, cpf, MenuLogin.canal);

                        MetodosDB.salvar(corrente);
                    } else if (tipoConta == 1) {
                        ContaPoupanca poupanca = (ContaPoupanca) conta;
                        MenuContaPoupanca.exibirMenu(scanner, poupanca, cpf, MenuLogin.canal);

                        MetodosDB.salvar(poupanca);
                    } else if (tipoConta == 2) {
                        ContaSalario salario = (ContaSalario) conta;
                        MenuContaSalario.exibirMenu(scanner, salario, cpf, MenuLogin.canal);

                        MetodosDB.salvar(salario);
                    } else {
                        System.out.println("Erro interno. Encerrando sessão.");
                        return;
                    }
                }

                return;
            }
        }

    }

}
