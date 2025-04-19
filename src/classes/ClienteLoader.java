package classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ClienteLoader {

    public static List<Cliente> lerClientesDeArquivo(String caminhoArquivo) {
        List<Cliente> clientes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                System.out.println("Lendo linha: " + linha);  // <- DEBUG

                String[] partes = linha.split(",");
                System.out.println("Partes: " + Arrays.toString(partes));  // <- DEBUG

                if (partes.length == 7) {
                    try {
                        String cpf = partes[0].trim();
                        String nome = partes[1].trim();
                        String senha = partes[2].trim();
                        int nroConta = Integer.parseInt(partes[3].trim());
                        BigDecimal saldo = new BigDecimal(partes[4].trim());
                        LocalDateTime dataAbertura = LocalDateTime.parse(partes[5].trim());
                        String tipoConta = partes[6].trim().toLowerCase();

                        Conta conta = switch (tipoConta) {
                            case "corrente" -> new ContaCorrente(senha, nroConta, saldo, dataAbertura);
                            case "poupanca" -> new ContaPoupanca(senha, nroConta, saldo, dataAbertura);
                            case "salario" -> new ContaSalario(senha, nroConta, saldo, dataAbertura);
                            default -> null;
                        };

                        if (conta != null) {
                            Cliente cliente = new Cliente(cpf, nome, conta);
                            clientes.add(cliente);

                            // debug
                            System.out.println("Cliente carregado: " + cliente.getCpf() + " - " + cliente.getNome() +
                                    " | Tipo: " + tipoConta + " | Conta: " + conta.getNro_conta());
                        } else {
                            System.out.println("Tipo de conta inválido: " + tipoConta);
                        }
                    } catch (Exception e) {
                        System.out.println("Erro ao processar linha: " + e.getMessage());
                    }
                } else {
                    System.out.println("Linha com formato inválido (esperado 7 partes): " + linha);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }

        return clientes;
    }
}
