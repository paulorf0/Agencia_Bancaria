package classes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MetodosDB {
    public static String dbNome;

    private static String puxarDados() {
        StringBuilder dadosConcatenados = new StringBuilder();
        try (FileInputStream file = new FileInputStream(dbNome);
                DataInputStream arq = new DataInputStream(file)) {

            while (arq.available() > 0) {
                String msg = arq.readUTF();
                dadosConcatenados.append(msg);
            }
        } catch (IOException e) {
            return ".";
        }

        return dadosConcatenados.toString();
    }

    public static String consultarSenha(String CPF) {
        return null;
    };

    // Vê o tipo pelo o atributo tipoConta e então converte
    public static Conta consultar(String CPF) {
        return null;
    };

    // verifica se uma conta já existe no sistema. 1 se existe, 0 se não existe
    public static int consultarExiste(String CPF) {
        String inf = puxarDados();
        if (inf.equals(".")) {
            System.out.println("\nOcorreu um erro interno.");
            return 0;
        }
        String[] blocos = inf.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF)) {
                    return 1;
                }
            }
        }

        return 0;
    };

    public static void salvar(Cliente cliente) {
        if (consultarExiste(cliente.getCpf()) == 1) {
            return;
        }

        try (FileOutputStream file = new FileOutputStream(dbNome, true);
                DataOutputStream arq = new DataOutputStream(file)) {

            int tipoConta = cliente.getConta().getTipoConta();
            Conta conta = cliente.getConta();

            String informacao = cliente.getCpf() + ";" + cliente.getNome() + ";" + conta.getSenha() + ";"
                    + conta.getNro_conta() + ";"
                    + conta.getSaldo() + ";" + conta.getData_abertura().toString() + ";"
                    + conta.getUlt_movimentacao().toString() + ";"
                    + conta.getTipoConta() + ";" + conta.getNro_agencia().toString() + ";";

            if (tipoConta == 0) {
                ContaCorrente contaCorrente = (ContaCorrente) conta;
                informacao += contaCorrente.getLimite_cheque_especial().toString() + ";"
                        + contaCorrente.getTaxa_administrativa().toString() + "*";
            } else if (tipoConta == 1) {
                ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
                informacao += contaPoupanca.getRendimento().toString() + "*";
            } else {
                ContaSalario contaSalario = (ContaSalario) conta;
                informacao += contaSalario.getLimite_saque().toString() + ";" + contaSalario.getLimite_transf() + "*";
            }

            arq.writeUTF(informacao);

        } catch (IOException e) {
            System.out.println("\nProblema interno.");

        }

    };
}
