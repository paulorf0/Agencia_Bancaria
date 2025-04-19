package classes;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Cliente> clientes = ClienteLoader.lerClientesDeArquivo("cliente.txt");

        Cliente clienteAutenticado = null;

        System.out.println("=== Login no Banco Digital ===");

        while (clienteAutenticado == null) {
            System.out.print("CPF: ");
            String cpf = sc.nextLine();

            System.out.print("Senha: ");
            String senha = sc.nextLine();

            for (Cliente cliente : clientes) {
                if (cliente.getCpf().equals(cpf)) {
                    if (cliente.getConta().getSenha().equals(senha)) {
                        clienteAutenticado = cliente;
                        break;
                    }
                }
            }

            if (clienteAutenticado == null) {
                System.out.println("\n CPF ou senha incorretos. Tente novamente.\n");
            }
        }

        Conta conta = clienteAutenticado.getConta();
        String tipoConta = conta instanceof ContaCorrente ? "Conta Corrente"
                : conta instanceof ContaPoupanca ? "Conta Poupança"
                : conta instanceof ContaSalario ? "Conta Salário"
                : "Tipo de Conta Desconhecido";

        System.out.println("\n Login efetuado com sucesso!");
        System.out.println("Bem-vindo(a), " + clienteAutenticado.getNome());
        System.out.println("Tipo de conta: " + tipoConta);

        sc.close();
    }
}
