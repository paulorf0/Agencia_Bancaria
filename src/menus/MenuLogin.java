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
            System.out.println("2. Cadastrar novo cliente");
            System.out.println("3. Cadastrar novo funcionario");
            System.out.println("4. Cadastrar novo gerente");
            System.out.println("5. Cadastrar nova agencia");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    fazerLogin(scanner);

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
                    sair = true;
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
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        if (!ValidarCPF.validar(cpf)) {
            Utils.limparConsole();
            System.out.println("CPF invalido. Saindo...");
            return null;
        }

        // VALIDAR SE OU O CPF JÁ É FUNCIONÁRIO OU GERENTE, OU SE O CPF JÁ TEM DUAS
        // CONTAS CADASTRADAS.
        List<Integer> tiposCPF = MetodosDB.consultarTipoConta(cpf);
        if (tiposCPF.size() > 0 && (tiposCPF.size() == 2 || tiposCPF.contains(3) || tiposCPF.contains(4))) {
            System.out.println("Não é possível fazer cadastro desse CPF.");
            return null;
        }

        Endereco end = capturarEndereco(scanner);
        System.out.print("Nome completo: ");
        String nomeCompleto = scanner.nextLine();

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
            if (!senhas.getFirst().equals(senhas.getLast())) {
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
            } else {
                if (!senhas.getFirst().equals(senha)) {
                    Utils.limparConsole();
                    System.out.println("Senha ou CPF invalido!");
                    return;
                }

                Utils.limparConsole();
                System.out.println("Duas contas cadastradas!\n");
                List<Integer> tipos = MetodosDB.consultarTipoConta(cpf);
                List<String> nros = MetodosDB.consultarNroConta(cpf);

                String msg1 = "";
                String msg2 = "";
                boolean eFuncionario = tipos.contains(3) || tipos.contains(4);
                int funcionarioEscolha = -1; // 3 para Funcionário, 4 para Gerente, se a opção 2 for staff

                if (eFuncionario) {
                    // Um usuário não pode ter conta de Funcionário E Gerente no mesmo CPF.
                    String nroConta;
                    int tipoDaConta;

                    if (tipos.get(0) == 3 || tipos.get(0) == 4) { // A primeira conta na lista é funcionario
                        funcionarioEscolha = tipos.get(0);
                        nroConta = nros.get(1);
                        tipoDaConta = tipos.get(1);
                        msg2 = (funcionarioEscolha == 3) ? "2. Funcionario" : "2. Gerente";
                    } else { // A segunda conta na lista deve ser funcionário
                        funcionarioEscolha = tipos.get(1);
                        nroConta = nros.get(0);
                        tipoDaConta = tipos.get(0);
                        msg2 = (funcionarioEscolha == 3) ? "2. Funcionario" : "2. Gerente";
                    }
                    String stringTipoRegular = tipoDaConta == 0 ? "Corrente"
                            : (tipoDaConta == 1) ? "Poupança"
                                    : (tipoDaConta == 2) ? "Salário" : "Desconhecida";
                    msg1 = "1. " + nroConta + " (" + stringTipoRegular + ")";

                } else {
                    // Caso padrão: duas contas regulares
                    int tipo1 = tipos.get(0);
                    int tipo2 = tipos.get(1);
                    String stringTipo1 = tipo1 == 0 ? "Corrente"
                            : (tipo1 == 1) ? "Poupança" : (tipo1 == 2) ? "Salário" : "Desconhecido";
                    String stringTipo2 = tipo2 == 0 ? "Corrente"
                            : (tipo2 == 1) ? "Poupança" : (tipo2 == 2) ? "Salário" : "Desconhecido";
                    msg1 = "1. " + nros.get(0) + " (" + stringTipo1 + ")";
                    msg2 = "2. " + nros.get(1) + " (" + stringTipo2 + ")";
                }

                System.out.println(msg1);
                System.out.println(msg2);

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

    private static void cadastrarCliente(Scanner scanner) {
        Utils.limparConsole();

        System.out.print("Digite o CPF: ");
        String cpf = scanner.nextLine();

        if (!ValidarCPF.validar(cpf)) {
            System.out.println("CPF inválido. Cadastro não realizado.");
            return;
        }

        if (MetodosDB.consultarExiste(cpf) == 2) {
            System.out.println("Duas contas já foram cadastrados nesse cpf.");
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

        Cliente novo_cliente;
        if (tipoConta == 0) {
            ContaCorrente corrente = new ContaCorrente(senha, nro_agencia);
            novo_cliente = new Cliente(cpf, nome, corrente);
        } else if (tipoConta == 1) {
            ContaPoupanca poupanca = new ContaPoupanca(senha, nro_agencia);
            novo_cliente = new Cliente(cpf, nome, poupanca);
        } else {
            ContaSalario salario = new ContaSalario(senha, nro_agencia);
            novo_cliente = new Cliente(cpf, nome, salario);
        }
        MetodosDB.salvar(novo_cliente);
    }
}
