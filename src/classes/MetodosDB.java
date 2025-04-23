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

import enums.Canal;
import enums.TipoTransacao;

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

    public static int consultarTipoConta(String CPF) {

        String dados = puxarDados();

        if (dados == null)
            return -1;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF)) {
                    int tipoConta = Integer.parseInt(campos[7]);
                    return tipoConta;
                }
            }
        }

        return -1;
    }

    public static String consultarSenha(String CPF) {
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
    }

    public static Funcionario consultarFuncionario(String CPF) {
        return null;
    }

    // Vê o tipo pelo o atributo tipoConta e então converte
    public static Conta consultarConta(String CPF) {
        String dados = puxarDados();

        if (dados == null)
            return null;

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
                    int nroAgencia = Integer.parseInt(campos[8]);

                    List<Transacao> transacoes_conta = new ArrayList<>();
                    if (!campos[9].isEmpty()) {
                        String[] transacoes = campos[9].split("\\:");
                        for (String inf : transacoes) {
                            String[] campos_transacao = inf.split(",", -1);

                            UUID da_conta = UUID.fromString(campos_transacao[0]);
                            UUID para_conta = campos_transacao[1].isEmpty() ? null
                                    : UUID.fromString(campos_transacao[1]);
                            LocalDateTime data = LocalDateTime.parse(campos_transacao[2]);
                            TipoTransacao tipo = TipoTransacao.valueOf(campos_transacao[3]);
                            BigDecimal valor = new BigDecimal(campos_transacao[4]);
                            Canal canal = Canal.valueOf(campos_transacao[5]);

                            Transacao t;

                            if (para_conta == null) {
                                t = new Transacao(da_conta, data, tipo, valor, canal);
                            } else {
                                t = new Transacao(da_conta, para_conta, data, tipo, valor, canal);
                            }

                            transacoes_conta.add(t);
                        }
                    }

                    if (tipoConta == 0) { // Conta Corrente
                        BigDecimal limiteChequeEspecial = new BigDecimal(campos[9]);
                        BigDecimal taxaAdministrativa = new BigDecimal(campos[10]);
                        ContaCorrente contaCorrente = new ContaCorrente(senha, saldo, dataAbertura,
                                limiteChequeEspecial, taxaAdministrativa, nroAgencia, transacoes_conta);
                        contaCorrente.setNro_conta(nroConta); // Ajusta o UUID
                        contaCorrente.setUlt_movimentacao(ultMovimentacao);
                        return contaCorrente;
                    } else if (tipoConta == 1) { // Conta Poupança
                        BigDecimal rendimento = new BigDecimal(campos[9]);
                        ContaPoupanca contaPoupanca = new ContaPoupanca(senha, saldo, dataAbertura,
                                nroAgencia, transacoes_conta);
                        contaPoupanca.setNro_conta(nroConta);
                        contaPoupanca.setUlt_movimentacao(ultMovimentacao);
                        contaPoupanca.setRendimento(rendimento);
                        return contaPoupanca;
                    } else if (tipoConta == 2) { // Conta Salário
                        BigDecimal limiteSaque = new BigDecimal(campos[9]);
                        BigDecimal limiteTransf = new BigDecimal(campos[10]);
                        ContaSalario contaSalario = new ContaSalario(senha, saldo, dataAbertura, limiteSaque,
                                limiteTransf,
                                nroAgencia, transacoes_conta);
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

        for (int j = 0; j < conta.getHist().size(); j++) {
            Transacao t = conta.getHist().get(j);
            sb.append(t.getDa_conta()).append(',')
                    .append(t.getPara_conta() != null ? t.getPara_conta() : "") // UUID para_conta ou vazio
                    .append(',').append(t.getData()) // LocalDateTime
                    .append(',').append(t.getTipo()) // TipoTransacao
                    .append(',').append(t.getValor()) // BigDecimal valor
                    .append(',').append(t.getCanal()); // Canal
            if (j < conta.getHist().size() - 1) // Se for a ultima transacao para gravar, não é adicionado o marcador
                                                // ":"
                sb.append(":");
        } // Se não tiver transações, fica ...;;...;*
        sb.append(';');

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
            try (FileOutputStream file = new FileOutputStream(dbNome);
                    DataOutputStream arq = new DataOutputStream(file)) {
                for (String b : blocos) {
                    arq.writeUTF(b + "*");
                }
            } catch (IOException e) {
                System.err.println("Erro interno.");
            }
        } else {
            try (FileOutputStream file = new FileOutputStream(dbNome, true);
                    DataOutputStream arq = new DataOutputStream(file)) {
                arq.writeUTF(blocoNovo);
            } catch (IOException e) {
                System.err.println("Erro interno.");
            }
        }
    };

    public static void salvar(Conta conta) {
        String inf = puxarDados();
        if (inf == null) {
            System.err.println("Erro interno ao ler o DB");
            return;
        }

        String[] linhas = inf.split("\\*");
        List<String> blocos = new ArrayList<>();
        for (String bloco : linhas) {
            if (!bloco.trim().isEmpty()) {
                blocos.add(bloco);
            }
        }

        boolean existe = false;
        for (int i = 0; i < blocos.size(); i++) {
            String[] campos = blocos.get(i).split(";");
            if (campos.length > 3 && campos[3].trim().equals(conta.getNro_conta().toString())) {
                String cpf = campos[0].trim();
                String nome = campos[1].trim();

                StringBuilder sb = new StringBuilder();
                sb.append(cpf).append(';')
                        .append(nome).append(';')
                        .append(conta.getSenha()).append(';')
                        .append(conta.getNro_conta()).append(';')
                        .append(conta.getSaldo()).append(';')
                        .append(conta.getData_abertura()).append(';')
                        .append(conta.getUlt_movimentacao()).append(';')
                        .append(conta.getTipoConta()).append(';')
                        .append(conta.getNro_agencia()).append(';');

                for (int j = 0; j < conta.getHist().size(); j++) {
                    Transacao t = conta.getHist().get(j);
                    sb.append(t.getDa_conta()).append(',')
                            .append(t.getPara_conta() != null ? t.getPara_conta() : "") // UUID para_conta ou vazio
                            .append(',').append(t.getData()) // LocalDateTime
                            .append(',').append(t.getTipo()) // TipoTransacao
                            .append(',').append(t.getValor()) // BigDecimal valor
                            .append(',').append(t.getCanal()); // Canal

                }
                sb.append(';');

                switch (conta.getTipoConta()) {
                    case 0:
                        ContaCorrente cc = (ContaCorrente) conta;
                        sb.append(cc.getLimite_cheque_especial()).append(';')
                                .append(cc.getTaxa_administrativa()).append('*');
                        break;
                    case 1:
                        ContaPoupanca cp = (ContaPoupanca) conta;
                        sb.append(cp.getRendimento()).append('*');
                        break;
                    case 2:
                        ContaSalario cs = (ContaSalario) conta;
                        sb.append(cs.getLimite_saque()).append(';')
                                .append(cs.getLimite_transf()).append('*');
                        break;
                    default:

                        System.out.println("\nErro interno.");
                        return;
                }

                String novoBlocoSemAsterisco = sb.toString().substring(0, sb.length() - 1);
                blocos.set(i, novoBlocoSemAsterisco);
                existe = true;
                break;
            }
        }

        if (!existe) {
            System.out.println("\nErro interno.");
            return;
        }

        try (FileOutputStream file = new FileOutputStream(dbNome);
                DataOutputStream arq = new DataOutputStream(file)) {
            for (String b : blocos) {
                arq.writeUTF(b + "*");
            }
        } catch (IOException e) {
            System.out.println("\nErro interno.");
        }
    }

    public static void salvar(Funcionario func) {
        String inf = puxarDados();
        if (inf == null) {
            System.err.println("Erro interno ao ler o DB");
            return;
        }

        int tipoFunc = func.getCargo() == "Gerente" ? 4 : 3;

        StringBuilder sb = new StringBuilder();
        sb.append(func.cpf).append(';') // cpf
                .append(func.nomeCompleto).append(';') // nome
                .append(func.senha).append(';') // senha
                .append(func.rg).append(';') // rg
                .append(func.dataNascimento).append(';') // data_nasc
                .append(func.estadoCivil).append(';') // estado_civil
                .append(func.endereco.getCidade()).append(';') // cidade
                .append(tipoFunc).append(';') // tipoConta (3 ou 4)
                .append(func.endereco.getEstado()).append(';') // estado
                .append(func.endereco.getBairro()).append(';') // bairro
                .append(func.endereco.getNro_local()).append(';')// nro_casa
                .append(func.getNro_cart()).append(';') // nro_cart
                .append(func.getCargo()).append(';') // cargo
                .append(func.getNro_agencia()).append(';') // nro_agencia ← adicionado aqui
                .append(func.getSexo()).append(';') // sexo
                .append(func.getSalario()).append(';') // salario
                .append(func.getAnoIngresso()).append(';'); // ano_ingresso

        if (tipoFunc == 4) {
            Gerente g = (Gerente) func;
            sb.append(g.getData_ingr_gerente()).append(';')
                    .append(g.getComissao()).append(';');
            for (String curso : g.getCursos()) {
                sb.append(curso).append(',');
            }
        }
        sb.append('*');
        String blocoNovo = sb.toString();

        String[] partes = inf.split("\\*");
        List<String> blocos = new ArrayList<>();
        for (String b : partes) {
            if (!b.trim().isEmpty())
                blocos.add(b);
        }

        boolean existe = false;
        for (int i = 0; i < blocos.size(); i++) {
            String[] campos = blocos.get(i).split(";", 2);
            if (campos[0].trim().equals(func.cpf)) {
                blocos.set(i, blocoNovo.substring(0, blocoNovo.length() - 1));
                existe = true;
                break;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(dbNome, !existe);
                DataOutputStream dos = new DataOutputStream(fos)) {
            if (existe) {
                for (String b : blocos) {
                    dos.writeUTF(b + "*");
                }
            } else {
                dos.writeUTF(blocoNovo);
            }
        } catch (IOException e) {
            System.err.println("Erro interno ao escrever o DB");
        }
    }
}
