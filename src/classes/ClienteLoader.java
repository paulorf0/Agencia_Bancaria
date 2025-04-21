package classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteLoader {

    public static List<Cliente> lerClientesDeArquivo(String caminhoArquivo) {
        List<Cliente> clientes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");

                if (partes.length == 7) {
                    String cpf = partes[0];
                    String nome = partes[1];
                    String senha = partes[2];
                    int nroConta = Integer.parseInt(partes[3]);
                    BigDecimal saldo = new BigDecimal(partes[4]);
                    LocalDateTime dataAbertura = LocalDateTime.parse(partes[5]);
                    String tipoConta = partes[6].toLowerCase();

                    Conta conta = switch (tipoConta) {
                        case "corrente" -> new ContaCorrente(senha, nroConta, saldo, dataAbertura);
                        case "poupanca" -> new ContaPoupanca(senha, nroConta, saldo, dataAbertura);
                        case "salario" -> new ContaSalario(senha, nroConta, saldo, dataAbertura);
                        default -> null;
                    };

                    if (conta != null) {
                        Cliente cliente = new Cliente(cpf, nome, conta);
                        clientes.add(cliente);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }

        return clientes;
    }
}
