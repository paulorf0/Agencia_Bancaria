package menus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import classes.Agencia;
import classes.Cliente;
import classes.ContaCorrente;
import classes.ContaPoupanca;
import classes.ContaSalario;
import classes.Endereco;
import classes.Funcionario;
import classes.Gerente;
import enums.EstadoCivil;
import enums.Sexo;
import outros.MetodosDB;
import outros.Utils;
import outros.ValidarCPF;

public class MenuCadastro {
    public static void cadastrarAgencia(Scanner scanner) {
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

    public static void cadastrarFuncionario(Scanner scanner) {
        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();
        Funcionario func = capturarInformacoes(scanner);
        if (func == null)
            return;
        func.setCargo(cargo);
        MetodosDB.salvar(func);
        System.out.println("Funcionário cadastrado com sucesso!");
    }

    public static void cadastrarCliente(Scanner scanner) {
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

    public static void cadastrarGerente(Scanner scanner) {

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

    public static Funcionario capturarInformacoes(Scanner scanner) {
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        if (!ValidarCPF.validar(cpf)) {
            Utils.limparConsole();
            System.out.println("CPF invalido. Saindo...");
            return null;
        }

        // VALIDAÇÃO DE QUE SE O CPF FORNECIDO JÁ É FUNCIONÁRIO OU GERENTE, OU SE O CPF
        // JÁ TEM DUAS
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

    public static Endereco capturarEndereco(Scanner scanner) {
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

}
