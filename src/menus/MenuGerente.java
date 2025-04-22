package menus;

import java.util.Scanner;

import classes.Conta;
import classes.Funcionario;
import classes.Gerente;
import classes.MetodosDB;
import outros.Utils;

public class MenuGerente {
    public static void Menu(Scanner scanner, String CPF) {
        boolean identificar = true;

        while (identificar) {
            System.out.println(
                    "1. Consultar minhas informacoes\n2. Consultar uma conta\n3. Verificar historico de transacoes\n4. Sair");
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

                    if (MetodosDB.consultarExiste(cpf_dest) == 1) {
                        if (MetodosDB.consultarTipoConta(cpf_dest) > 2) {
                            System.out.println("Esse CPF pertence a um funcionario.");
                        } else {
                            Conta conta = MetodosDB.consultar(cpf_dest);
                            System.out.println(conta.consultarInf());
                        }
                    } else {
                        System.out.println("Cliente não cadastrado.");
                    }

                    break;
                case "3":
                    System.out.println("Digite o CPF da conta");
                    cpf_dest = scanner.nextLine();

                    Utils.limparConsole();
                    if (MetodosDB.consultarExiste(cpf_dest) == 1) {
                        if (MetodosDB.consultarTipoConta(cpf_dest) > 2)
                            System.out.println("Esse CPF pertence a um funcionario.");
                        else {

                            Conta conta = MetodosDB.consultar(cpf_dest);
                            System.out.println(
                                    "Transacoes da conta: " + conta.getNro_conta() + "\nCPF: " + cpf_dest + "\n");
                            System.out.println("--------------------------------------");
                            System.out.println("\n" + conta.consultarHist());
                            System.out.println("--------------------------------------");
                        }
                    } else
                        System.out.println("Cliente não cadastrado.");

                    break;
                case "4":
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
