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
import classes.MetodosDB;
import enums.Canal;
import enums.EstadoCivil;
import enums.Sexo;
import outros.Utils;
import outros.ValidarCPF;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

        while (!autenticado) {
            Utils.limparConsole();

            System.out.println("\n=== Banco Digital ===");
            System.out.println("1. Login");
            System.out.println("2. Cadastrar novo cliente");
            System.out.println("3. Cadastrar novo funcionario");
            System.err.println("4. Cadastrar novo gerente");
            System.err.println("5. Cadastrar nova agencia");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    autenticado = fazerLogin(scanner);
                    break;
                case "2":
                    cadastrarCliente(scanner);
                    break;
                case "3":
                    cadastrarFuncionario(scanner);
                    break;
                case "4":
                    cadastrarGerente(scanner);
                    break;
                case "5":
                    cadastrarAgencia(scanner);
                    break;
                case "6":
                    System.out.println("Saindo...");
                    autenticado = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void cadastrarAgencia(Scanner scanner) {
        Endereco end = capturarEndereco(scanner);
        System.out.print("Digite o nome da agencia: ");
        String nome = scanner.nextLine();

        int nro_agc;
        System.out.print("Digite o número da agência: ");
        while (true) {
            try {
                nro_agc = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }

        Agencia agencia = new Agencia(nro_agc, nome, end);
        MetodosDB.salvar(agencia);
    }

    private static void cadastrarFuncionario(Scanner scanner) {
        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();
        Funcionario func = capturarInformacoes(scanner);
        if (func == null)
            return;
        func.setCargo(cargo);
        MetodosDB.salvar(func);
        System.out.println("Funcionário cadastrado com sucesso!");
    }

    private static void cadastrarGerente(Scanner scanner) {

        Funcionario func = capturarInformacoes(scanner);
        if (func == null)
            return;

        System.out.print("Data de ingresso como gerente (yyyy-MM-dd): ");
        LocalDate data_ingr_gerente;
        while (true) {
            try {
                data_ingr_gerente = LocalDate.parse(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Data inválida. Tente novamente.");
            }
        }

        System.out.print("Cursos (separados por vírgula): ");
        List<String> cursos;
        while (true) {
            try {
                String cursosI = scanner.nextLine();
                cursos = List.of(cursosI.split(",\\s*"));
                break;
            } catch (Exception e) {
                System.out.println("Entrada inválida. Tente novamente.");
            }
        }

        System.out.print("Comissão: ");
        BigDecimal comissao;
        while (true) {
            try {
                comissao = new BigDecimal(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Tente novamente.");
            }
        }

        Gerente gerente = new Gerente(
                func.getNro_cart(),
                func.getNro_agencia(),
                func.getSexo(),
                func.getSalario(),
                func.getAnoIngresso(),
                data_ingr_gerente,
                cursos,
                comissao);

        gerente.setNomeCompleto(func.getNomeCompleto());
        gerente.setCpf(func.getCpf());
        gerente.setRg(func.getRg());
        gerente.setSenha(func.getSenha());
        gerente.setDataNascimento(func.getDataNascimento());
        gerente.setEndereco(func.getEndereco());
        gerente.setEstadoCivil(func.getEstadoCivil());

        MetodosDB.salvar(gerente);
    }

    private static Funcionario capturarInformacoes(Scanner scanner) {
        Endereco end = capturarEndereco(scanner);

        System.out.print("Nome completo: ");
        String nomeCompleto = scanner.nextLine();

        System.out.print("CPF: ");

        String cpf = scanner.nextLine();
        if (!ValidarCPF.validar(cpf)) {
            Utils.limparConsole();
            System.out.println("CPF invalido. Saindo...");
            return null;
        }

        System.out.print("RG: ");
        String rg = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        System.out.print("Data de nascimento (yyyy-MM-dd): ");
        LocalDate dataNascimento;
        while (true) {
            try {
                dataNascimento = LocalDate.parse(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Data inválida. Tente novamente.");
            }
        }

        System.out.print("Estado civil (SOLTEIRO, CASADO, DIVORCIADO, VIUVO): ");
        EstadoCivil estadoCivil;
        while (true) {
            try {
                estadoCivil = EstadoCivil.valueOf(scanner.nextLine().toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Estado civil inválido. Tente novamente.");
            }
        }

        System.out.print("Número da carteira de trabalho: ");
        int nro_cart;
        while (true) {
            try {
                nro_cart = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }

        System.out.print("Número da agência: ");
        int nro_agencia;
        while (true) {
            try {
                nro_agencia = Integer.parseInt(scanner.nextLine());

                if (MetodosDB.consultarAgencia(nro_agencia) == 0) {
                    System.out.println("Essa agencia não está cadastrada.");
                    return null;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }

        System.out.print("Sexo (MASCULINO, FEMININO): ");
        Sexo sexo;
        while (true) {
            try {
                sexo = Sexo.valueOf(scanner.nextLine().toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Sexo inválido. Tente novamente.");
            }
        }

        System.out.print("Salário: ");
        BigDecimal salario;
        while (true) {
            try {
                salario = new BigDecimal(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Tente novamente.");
            }
        }

        System.out.print("Ano de ingresso (yyyy-MM-dd): ");
        LocalDate anoIngresso;
        while (true) {
            try {
                anoIngresso = LocalDate.parse(scanner.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Data inválida. Tente novamente.");
            }
        }

        Funcionario funcionario = new Funcionario(nro_cart, nro_agencia, sexo, salario, anoIngresso);
        funcionario.setNomeCompleto(nomeCompleto);
        funcionario.setCpf(cpf);
        funcionario.setRg(rg);
        funcionario.setSenha(senha);
        funcionario.setDataNascimento(dataNascimento);
        funcionario.setEndereco(end);
        funcionario.setEstadoCivil(estadoCivil);

        return funcionario;
    }

    private static Endereco capturarEndereco(Scanner scanner) {
        System.out.print("Digite a cidade: ");
        String cidade = scanner.nextLine();

        System.out.print("Digite o estado: ");
        String estado = scanner.nextLine();

        System.out.print("Digite o bairro: ");
        String bairro = scanner.nextLine();

        int nro_local;
        System.out.print("Digite o número do local: ");
        while (true) {
            try {
                nro_local = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }

        return new Endereco(cidade, estado, bairro, nro_local);
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
                escolherCanal(scanner);
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

                if (MetodosDB.consultarAgencia(nro_agencia) == 0) {
                    System.out.println("Essa agencia não está cadastrada.");
                    return;
                }

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
