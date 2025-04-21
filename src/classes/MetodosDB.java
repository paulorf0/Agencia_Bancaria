package classes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MetodosDB {
    public static String dbNome;

    public static String dados() {
        return puxarDados();
    }

    private static String puxarDados() {
        StringBuilder dadosConcatenados = new StringBuilder();
        try (FileInputStream file = new FileInputStream(dbNome);
                DataInputStream arq = new DataInputStream(file)) {

            while (arq.available() > 0) {
                String msg = arq.readUTF();
                dadosConcatenados.append(msg);
            }
        } catch (IOException e) {
            return null;
        }

        return dadosConcatenados.toString();
    }

    public static String consultarSenha(String CPF) {
        if (consultarExiste(CPF) == 0)
            return null;

        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF))
                    return campos[2];
            }
        }

        return null;
    };

    // Vê o tipo pelo o atributo tipoConta e então converte
    public static Conta consultar(String CPF) {
        if (consultarExiste(CPF) == 0)
            return null;

        String dados = puxarDados();

        if (dados == null)
            return null;

        System.out.println("\n" + dados);
        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF)) {
                    String senha = campos[2];
                    UUID nroConta = UUID.fromString(campos[3]);
                    BigDecimal saldo = new BigDecimal(campos[4]);
                    LocalDateTime dataAbertura = LocalDateTime.parse(campos[5]);
                    LocalDateTime ultMovimentacao = LocalDateTime.parse(campos[6]);
                    int tipoConta = Integer.parseInt(campos[7]);
                    System.out.println("\n" + campos[8]);
                    UUID nroAgencia = UUID.fromString(campos[8]);

                    if (tipoConta == 0) { // Conta Corrente
                        BigDecimal limiteChequeEspecial = new BigDecimal(campos[9]);
                        BigDecimal taxaAdministrativa = new BigDecimal(campos[10]);
                        ContaCorrente contaCorrente = new ContaCorrente(senha, saldo, dataAbertura.toLocalDate(),
                                limiteChequeEspecial, taxaAdministrativa, nroAgencia);
                        contaCorrente.setNro_conta(nroConta); // Ajusta o UUID
                        contaCorrente.setUlt_movimentacao(ultMovimentacao);
                        return contaCorrente;
                    } else if (tipoConta == 1) { // Conta Poupança
                        BigDecimal rendimento = new BigDecimal(campos[9]);
                        ContaPoupanca contaPoupanca = new ContaPoupanca(senha, saldo, dataAbertura,
                                nroAgencia);
                        contaPoupanca.setNro_conta(nroConta);
                        contaPoupanca.setUlt_movimentacao(ultMovimentacao);
                        contaPoupanca.setRendimento(rendimento);
                        return contaPoupanca;
                    } else if (tipoConta == 2) { // Conta Salário
                        BigDecimal limiteSaque = new BigDecimal(campos[9]);
                        BigDecimal limiteTransf = new BigDecimal(campos[10]);
                        ContaSalario contaSalario = new ContaSalario(senha, saldo, dataAbertura, limiteSaque,
                                limiteTransf,
                                nroAgencia);
                        contaSalario.setNro_conta(nroConta); // Ajusta o UUID
                        contaSalario.setUlt_movimentacao(ultMovimentacao);
                        return contaSalario;
                    }
                }
            }
        }

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
        String inf = puxarDados();
        if (inf == null) {
            System.err.println("Erro interno ao ler o DB");
            return;
        }

        int tipoConta = cliente.getConta().getTipoConta();
        Conta conta = cliente.getConta();

        StringBuilder sb = new StringBuilder();
        sb.append(cliente.getCpf()).append(';')
                .append(cliente.getNome()).append(';')
                .append(conta.getSenha()).append(';')
                .append(conta.getNro_conta()).append(';')
                .append(conta.getSaldo()).append(';')
                .append(conta.getData_abertura()).append(';')
                .append(conta.getUlt_movimentacao()).append(';')
                .append(conta.getTipoConta()).append(';')
                .append(conta.getNro_agencia()).append(';');

        if (tipoConta == 0) {
            ContaCorrente contaCorrente = (ContaCorrente) conta;
            sb.append(contaCorrente.getLimite_cheque_especial().toString()).append(";")
                    .append(contaCorrente.getTaxa_administrativa().toString()).append("*");
        } else if (tipoConta == 1) {
            ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
            sb.append(contaPoupanca.getRendimento().toString()).append("*");
        } else {
            ContaSalario contaSalario = (ContaSalario) conta;
            sb.append(contaSalario.getLimite_saque().toString()).append(";")
                    .append(contaSalario.getLimite_transf().toString()).append("*");
        }

        String[] linha = inf.split("\\*");
        List<String> blocos = new ArrayList<>();
        for (String bloco : linha) {
            if (!bloco.trim().isEmpty()) {
                blocos.add(bloco);
            }
        }

        String blocoNovo = sb.toString();
        boolean existe = false;
        for (int i = 0; i < blocos.size(); i++) {
            String[] campos = blocos.get(i).split(";", 2);
            if (campos[0].trim().equals(cliente.getCpf())) {
                blocos.set(i, blocoNovo.substring(0, blocoNovo.length() - 1));
                existe = true;
                break;
            }
        }

        if (existe) {
            // 5a) reescreve tudo: modo truncate (remove o antigo)
            try (FileOutputStream file = new FileOutputStream(dbNome);
                    DataOutputStream arq = new DataOutputStream(file)) {
                for (String b : blocos) {
                    arq.writeUTF(b + "*");
                }
            } catch (IOException e) {
                System.err.println("Erro ao reescrever o DB: " + e.getMessage());
            }
        } else {
            // 5b) não existia: faz append normal
            try (FileOutputStream file = new FileOutputStream(dbNome, true);
                    DataOutputStream arq = new DataOutputStream(file)) {
                arq.writeUTF(blocoNovo);
            } catch (IOException e) {
                System.err.println("Erro ao adicionar novo cliente: " + e.getMessage());
            }
        }
    };
}
