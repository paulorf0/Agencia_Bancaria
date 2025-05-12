package outros;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Utils {
    public static void limparConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static String nro_conta_transferencia(String cpf, Scanner scanner) {
        List<Integer> tipos = MetodosDB.consultarTipoConta(cpf);
        List<String> nros = MetodosDB.consultarNroConta(cpf);

        String msg1 = "";
        String msg2 = "";
        boolean eFuncionario = tipos.contains(3) || tipos.contains(4);

        if (tipos.size() > 1) {// O CLIENTE TEM DUAS CONTAS CADASTRADAS.
            if (eFuncionario) {
                if (tipos.contains(2)) {
                    return null;
                }
                // Caso 2: Funcionário/Gerente e a primeira conta é funcionario (índice 0 é 3 ou
                // 4)
                if (tipos.get(0) == 3 || tipos.get(0) == 4) {
                    // Retorna a conta regular (índice 1)
                    return nros.get(1);
                }
                // Caso 3: Funcionário/Gerente e a segunda conta é funcionario (índice 1 é 3 ou
                // 4)
                if (tipos.get(1) == 3 || tipos.get(1) == 4) {
                    // Retorna a conta regular (índice 0)
                    return nros.get(0);
                }

                return null; // Por segurança.
            }

            if (!eFuncionario) {
                if (tipos.contains(2)) {
                    if (tipos.getFirst() == 2 && tipos.getLast() == 2)
                        return null; // Sem contas válidas para transferência.
                    else if (tipos.getFirst() == 2)
                        return nros.getLast();
                    else
                        return nros.getFirst();
                } else {
                    // Caso padrão: duas contas regulares
                    int tipo1 = tipos.get(0);
                    int tipo2 = tipos.get(1);
                    String stringTipo1 = tipo1 == 0 ? "Corrente"
                            : (tipo1 == 1) ? "Poupança" : "Desconhecido";
                    String stringTipo2 = tipo2 == 0 ? "Corrente"
                            : (tipo2 == 1) ? "Poupança" : "Desconhecido";
                    msg1 = "1. " + nros.get(0) + " (" + stringTipo1 + ")";
                    msg2 = "2. " + nros.get(1) + " (" + stringTipo2 + ")";

                    System.out.println("Duas contas regulares para consultar: ");
                    System.out.println(msg1);
                    System.out.println(msg2);
                }

            }

        } else if (tipos.getFirst() != 2) // Caso em que o cliente só tem uma conta cadastrada.
            return nros.getFirst();
        else
            return null;

        // Só alcança esse trecho de código se tiver duas contas válidas para
        // transferência.
        System.out.print("Escolha qual conta acessar: ");
        String opcao = scanner.nextLine();
        int index;
        try {
            index = Integer.parseInt(opcao);
            if (index < 1 || index > 2) {
                System.out.println("Numero inválido");
                return null;
            }

        } catch (NumberFormatException e) {
            System.out.println("Numero inválido");
            return null;
        }

        return nros.get(index - 1);
    }

    // Retorna o tipo ou -1 se houver erro.
    public static int capturar_tipo(List<Integer> tipo, Scanner scanner) {
        if (tipo.contains(0)) {
            System.out.println("0 - Conta Corrente");
        }
        if (tipo.contains(1)) {
            System.out.println("1 - Conta Poupança");
        }
        if (tipo.contains(2)) {
            System.out.println("2 - Conta Salário");
        }
        System.out.print("Digite o número correspondente ao tipo de conta: ");
        int tipoEscolhido;
        try {
            tipoEscolhido = Integer.parseInt(scanner.nextLine());
            if (tipo.contains(tipoEscolhido)) {
                return -1;
            } else {
                System.out.println("Opção inválida. Escolha novamente:");
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return tipoEscolhido;
    }

    public static int info_contas_login(String cpf, List<Integer> tipos, List<String> nros) {
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

        return funcionarioEscolha;
    }

    public static String info_contas_menu_gerente(String cpf, Scanner scanner) {
        List<Integer> tipos = MetodosDB.consultarTipoConta(cpf);
        List<String> nros = MetodosDB.consultarNroConta(cpf);

        String msg1 = "";
        String msg2 = "";
        boolean eFuncionario = tipos.contains(3) || tipos.contains(4);

        if (tipos.size() > 1) // O CLIENTE TEM DUAS CONTAS CADASTRADAS.
            if (eFuncionario) {
                // O CLIENTE QUE FOR CONSULTADO NO MENU DE GERENTE É UM FUNCIONÁRIO. ENTÃO ELE
                // SÓ TEM UMA CONTA REGULAR DISPONÍVEL PARA CONSULTA.
                String nroConta;

                if (tipos.get(0) == 3 || tipos.get(0) == 4)
                    nroConta = nros.get(1);
                else
                    nroConta = nros.get(0);

                return nroConta; // RETORNANDO NRO DA CONTA PARA CONSULTA.
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
        else {
            if (tipos.getFirst() < 3)
                return nros.getFirst();
            else
                return null;
        }
        System.out.println("Duas contas regulares para consultar: ");
        System.out.println(msg1);
        System.out.println(msg2);

        System.out.print("Escolha qual conta acessar: ");
        String opcao = scanner.nextLine();
        int index;
        try {
            index = Integer.parseInt(opcao);
            if (index < 1 || index > 2) {
                System.out.println("Numero inválido");
                return null;
            }

        } catch (NumberFormatException e) {
            System.out.println("Numero inválido");
            return null;
        }

        return nros.get(index - 1);
    }

    public static String formatoData(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"));
        return data.format(formatter);
    }
}
