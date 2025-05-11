package outros;

import java.util.List;
import java.util.Scanner;

public class Utils {
    public static void limparConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Retorna o tipo ou -1 se houver erro.
    public static int capturar_tipo_transf(List<Integer> tipo, Scanner scanner) {
        if (tipo.contains(0)) {
            System.out.println("0 - Conta Corrente");
        }
        if (tipo.contains(1)) {
            System.out.println("1 - Conta Poupança");
        }
        // if (tipo.contains(2)) {
        // System.out.println("2 - Conta Salário");
        // }
        System.out.print("Digite o número correspondente ao tipo de conta: ");
        int tipoEscolhido;
        try {
            tipoEscolhido = Integer.parseInt(scanner.nextLine());
            if (!tipo.contains(tipoEscolhido)) {
                return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return tipoEscolhido;
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
}
