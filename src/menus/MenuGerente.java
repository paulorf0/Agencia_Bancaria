package menus;

import java.util.Scanner;
import java.util.UUID;

import classes.Conta;
import classes.Funcionario;
import classes.Gerente;
import outros.MetodosDB;
import outros.Utils;

public class MenuGerente {
    public static void Menu(Scanner scanner, String CPF) {
        boolean identificar = true;

        while (identificar) {
            System.out.println("1. Consultar minhas informacoes");
            System.out.println("2. Consultar uma conta");
            System.out.println("3. Verificar historico de transacoes");
            System.out.println("4. Cadastrar Funcionário");
            System.out.println("5. Cadastrar Cliente");

            System.out.println("6. Reativar conta de cliente");
            System.out.println("7. Sair");

            String escolha = scanner.nextLine();

            Funcionario func = MetodosDB.consultarFuncionario(CPF);
            Gerente gerente = (Gerente) func;
            switch (escolha) {
                case "1":
                    System.out.println("Numero da carteira: " + gerente.getNro_cart());
                    System.out.println("Comissao: " + gerente.getComissao());
                    System.out.println("Salario: " + gerente.CalcSal());
                    System.out.println("Numero da agencia: " + gerente.getNro_agencia());
                    System.out.println("Ano de Ingresso: " + gerente.getAnoIngresso().toString());

                    String todosCursos = gerente.getCursosString();
                    if (todosCursos != null)
                        System.out.println("Cursos: " + todosCursos);
                    else
                        System.out.println("Nao tem cursos");

                    break;
                case "2":
                    System.out.println("Digite o CPF da conta");
                    String cpf_dest = scanner.nextLine();

                    if (MetodosDB.consultarExiste(cpf_dest) != 0) {
                        // SE O CLIENTE TIVER DUAS CONTAS, MOSTRA A OPÇÃO PARA PODER ESCOLHER QUAL CONTA
                        // CONSULTAR

                        // SE O CLIENTE FOR UM FUNCIONÁRIO OU GERENTE, ENTÃO CONSULTAR APENAS A CONTA
                        // QUE ELE TEM DISPONÍVEL (SE TIVER)

                        String nroString = Utils.info_contas_menu_gerente(cpf_dest, scanner);
                        UUID nro;
                        try {
                            nro = UUID.fromString(nroString);
                        } catch (Exception e) {
                            Utils.limparConsole();
                            System.out.println("Houve um erro interno na consulta.");
                            break;
                        }

                        // TALVEZ POSSA ADICIONAR MAIS INFORMAÇÕES NA CONSULTA.
                        Conta conta = MetodosDB.consultarConta(cpf_dest, nro);

                        if (conta.getSituacao() == 0)
                            System.out.println("\n--------- Conta inátiva --------- \n");

                        System.out.println(conta.consultarInf());
                    } else {
                        System.out.println("Cliente não cadastrado.");
                    }
                    break;
                case "3":
                    System.out.println("Digite o CPF da conta");
                    cpf_dest = scanner.nextLine();

                    Utils.limparConsole();
                    if (MetodosDB.consultarExiste(cpf_dest) != 0) {

                        String nroString = Utils.info_contas_menu_gerente(cpf_dest, scanner);
                        UUID nro;
                        try {
                            nro = UUID.fromString(nroString);
                        } catch (Exception e) {
                            Utils.limparConsole();
                            System.out.println("Houve um erro interno na consulta.");
                            break;
                        }
                        Conta conta = MetodosDB.consultarConta(cpf_dest, nro);

                        if (conta.getSituacao() == 0)
                            System.out.println("A conta está inativa. Antigas transações: \n");

                        System.out.println(
                                "Transacoes da conta: " + conta.getNro_conta() + "\nCPF: " + cpf_dest + "\n");
                        System.out.println("--------------------------------------");
                        System.out.println("\n" + conta.consultarHist());
                        System.out.println("--------------------------------------");

                    } else
                        System.out.println("Cliente não cadastrado.");

                    break;
                case "4":
                    MenuCadastro.cadastrarFuncionario(scanner);
                    break;
                case "5":
                    MenuCadastro.cadastrarCliente(scanner);
                    break;

                case "6":
                    System.out.println("Digite o CPF da conta");
                    cpf_dest = scanner.nextLine();

                    Utils.limparConsole();
                    if (MetodosDB.consultarExiste(cpf_dest) != 0) {

                        String nroString = Utils.info_contas_menu_gerente(cpf_dest, scanner);
                        UUID nro;
                        try {
                            nro = UUID.fromString(nroString);
                        } catch (Exception e) {
                            Utils.limparConsole();
                            System.out.println("Houve um erro interno na consulta.");
                            break;
                        }
                        Conta conta = MetodosDB.consultarConta(cpf_dest, nro);
                        if (conta.getSituacao() == 1) {
                            System.out.println("A conta já está ativa. Ultima movimentação em: ");
                            System.out.println(Utils.formatoData(conta.getUlt_movimentacao()) + "\n");
                            break;
                        } else
                            conta.setSituacao(1);

                        System.out.println("Conta de cliente reativada");
                        MetodosDB.salvar(conta);

                    }
                    break;
                case "7":
                    Utils.limparConsole();
                    System.out.println("Saindo...");
                    identificar = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }

        }

        Utils.limparConsole();
    }
}
