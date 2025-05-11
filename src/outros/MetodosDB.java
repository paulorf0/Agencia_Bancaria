package outros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import classes.Agencia;
import classes.Cliente;
import classes.Conta;
import classes.ContaCorrente;
import classes.ContaPoupanca;
import classes.ContaSalario;
import classes.Endereco;
import classes.Funcionario;
import classes.Gerente;
import classes.Transacao;
import enums.Canal;
import enums.EstadoCivil;
import enums.Sexo;
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

    public static int consultarTipoConta(UUID nro) {

        String dados = puxarDados();

        if (dados == null)
            return -1;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                UUID nroRegistro;
                try {
                    nroRegistro = UUID.fromString(campos[3].trim());
                } catch (Exception e) {
                    continue;
                }
                if (nroRegistro.equals(nro)) {
                    return Integer.parseInt(campos[7]);
                }
            }
        }

        return -1;
    }

    public static List<Integer> consultarTipoConta(String CPF) {

        String dados = puxarDados();

        if (dados == null)
            return null;

        List<Integer> tipos = new ArrayList<>();

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF)) {
                    tipos.add(Integer.parseInt(campos[7]));
                }
            }
        }

        return tipos;
    }

    public static List<String> consultarSenha(String CPF) {
        String dados = puxarDados();
        List<String> senhas = new ArrayList<>();
        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF))
                    senhas.add(campos[2]);
            }
        }

        return senhas;
    }

    public static List<String> consultarNroConta(String CPF) {
        String dados = puxarDados();
        List<String> nros = new ArrayList<>();
        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF))
                    nros.add(campos[3]);
            }
        }

        return nros;
    }

    public static Funcionario consultarFuncionario(String CPF) {
        String dados = puxarDados();
        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0 && campos[0].trim().equals(CPF)) {

                int tipoConta = Integer.parseInt(campos[7]);
                if (tipoConta < 3)
                    return null;

                // Campos comuns
                String cpf = campos[0].trim();
                String nome = campos[1];
                String senha = campos[2];
                String rg = campos[3];
                LocalDate dataNascimento = LocalDate.parse(campos[4], dateFmt);
                String estadoCivil = campos[5];
                String cidade = campos[6];
                String estado = campos[8];
                String bairro = campos[9];
                String nroCart = campos[11];
                String cargo = campos[12];
                int nroCasa = Integer.parseInt(campos[10]);
                int nroAgencia = Integer.parseInt(campos[13]);
                String sexo = campos[14];
                BigDecimal salario = new BigDecimal(campos[15]);
                LocalDate anoIngresso = LocalDate.parse(campos[16]);

                if (tipoConta == 3) {
                    Funcionario funcionario = new Funcionario(
                            Integer.parseInt(nroCart),
                            cargo,
                            nroAgencia,
                            Sexo.valueOf(sexo.toUpperCase()),
                            salario,
                            anoIngresso);
                    funcionario.setNomeCompleto(nome);
                    funcionario.setCpf(cpf);
                    funcionario.setRg(rg);
                    funcionario.setSenha(senha);
                    funcionario.setDataNascimento(dataNascimento);
                    funcionario.setEndereco(new Endereco(cidade, estado, bairro, nroCasa));
                    funcionario.setEstadoCivil(EstadoCivil.valueOf(estadoCivil.toUpperCase()));
                    return funcionario;
                } else if (tipoConta == 4) {
                    LocalDate dataIngressoGerente = LocalDate.parse(campos[17], dateFmt);
                    BigDecimal comissao = new BigDecimal(campos[18]);
                    List<String> cursos = new ArrayList<>();
                    if (campos.length > 19 && !campos[19].isEmpty()) {
                        String[] cursosArray = campos[19].split(",");
                        for (String curso : cursosArray) {
                            cursos.add(curso.trim());
                        }
                    }
                    Gerente gerente = new Gerente(
                            Integer.parseInt(nroCart),
                            nroAgencia,
                            Sexo.valueOf(sexo.toUpperCase()),
                            salario,
                            anoIngresso,
                            dataIngressoGerente,
                            cursos,
                            comissao);
                    gerente.setNomeCompleto(nome);
                    gerente.setCpf(cpf);
                    gerente.setRg(rg);
                    gerente.setSenha(senha);
                    gerente.setDataNascimento(dataNascimento);
                    gerente.setEndereco(new Endereco(cidade, estado, bairro, nroCasa));
                    gerente.setEstadoCivil(EstadoCivil.valueOf(estadoCivil.toUpperCase()));
                    return gerente;
                }
            }
        }
        return null;
    }

    // Buscar uma conta pelo tipo de conta e CPF.
    public static Conta consultarConta(String CPF, int tipoC) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter();

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                int tipoConta = Integer.parseInt(campos[7]);
                if (cpfRegistro.equals(CPF) && tipoConta == tipoC) {
                    if (tipoConta > 2)
                        return null;

                    String senha = campos[2];
                    UUID nroConta = UUID.fromString(campos[3]);
                    BigDecimal saldo = new BigDecimal(campos[4]);
                    LocalDateTime dataAbertura = LocalDateTime.parse(campos[5], formatter);
                    LocalDateTime ultMovimentacao = LocalDateTime.parse(campos[6], formatter);

                    int nroAgencia = Integer.parseInt(campos[8]);

                    List<Transacao> transacoes_conta = new ArrayList<>();
                    if (!campos[9].isEmpty()) {

                        String[] transacoes = campos[9].split("\\|");

                        for (String inf : transacoes) {
                            String[] campos_transacao = inf.split(",", -1);

                            UUID da_conta = UUID.fromString(campos_transacao[0]);
                            UUID para_conta = campos_transacao[1].isEmpty() ? null
                                    : UUID.fromString(campos_transacao[1]);
                            LocalDateTime data = LocalDateTime.parse(campos_transacao[2], formatter);
                            TipoTransacao tipo = TipoTransacao.valueOf(campos_transacao[3]);
                            BigDecimal valor = new BigDecimal(campos_transacao[4]);

                            int canal_value = Integer.parseInt(campos_transacao[5]);
                            Canal canal;

                            if (canal_value == 0)
                                canal = Canal.INTERNETBAKING;
                            else if (canal_value == 1)
                                canal = Canal.CAIXA_ELETRONICO;
                            else
                                canal = Canal.CAIXA_FISICO;

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
                        BigDecimal limiteChequeEspecial = new BigDecimal(campos[10]);
                        BigDecimal taxaAdministrativa = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);
                        ContaCorrente contaCorrente = new ContaCorrente(senha, saldo, dataAbertura,
                                limiteChequeEspecial, taxaAdministrativa, nroAgencia, transacoes_conta);
                        contaCorrente.setNro_conta(nroConta); // Ajusta o UUID
                        contaCorrente.setUlt_movimentacao(ultMovimentacao);
                        contaCorrente.setSituacao(situacao);
                        return contaCorrente;
                    } else if (tipoConta == 1) { // Conta Poupança
                        BigDecimal rendimento = new BigDecimal(campos[10]);
                        int situacao = Integer.parseInt(campos[11]);

                        ContaPoupanca contaPoupanca = new ContaPoupanca(senha, saldo, dataAbertura,
                                nroAgencia, transacoes_conta);
                        contaPoupanca.setNro_conta(nroConta);
                        contaPoupanca.setUlt_movimentacao(ultMovimentacao);
                        contaPoupanca.setRendimento(rendimento);
                        contaPoupanca.setSituacao(situacao);
                        return contaPoupanca;
                    } else if (tipoConta == 2) { // Conta Salário
                        BigDecimal limiteSaque = new BigDecimal(campos[10]);
                        BigDecimal limiteTransf = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);

                        ContaSalario contaSalario = new ContaSalario(senha, saldo, dataAbertura, limiteSaque,
                                limiteTransf,
                                nroAgencia, transacoes_conta);
                        contaSalario.setNro_conta(nroConta); // Ajusta o UUID
                        contaSalario.setUlt_movimentacao(ultMovimentacao);
                        contaSalario.setSituacao(situacao);
                        return contaSalario;
                    }
                }
            }
        }

        return null;
    };

    // Alterar para encontrar por número de conta.
    public static Conta consultarConta(String CPF) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter();

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                int tipoConta = Integer.parseInt(campos[7]);
                if (cpfRegistro.equals(CPF)) {
                    if (tipoConta > 2)
                        return null;

                    String senha = campos[2];
                    UUID nroConta = UUID.fromString(campos[3]);
                    BigDecimal saldo = new BigDecimal(campos[4]);
                    LocalDateTime dataAbertura = LocalDateTime.parse(campos[5], formatter);
                    LocalDateTime ultMovimentacao = LocalDateTime.parse(campos[6], formatter);

                    int nroAgencia = Integer.parseInt(campos[8]);

                    List<Transacao> transacoes_conta = new ArrayList<>();
                    if (!campos[9].isEmpty()) {

                        String[] transacoes = campos[9].split("\\|");

                        for (String inf : transacoes) {
                            String[] campos_transacao = inf.split(",", -1);

                            UUID da_conta = UUID.fromString(campos_transacao[0]);
                            UUID para_conta = campos_transacao[1].isEmpty() ? null
                                    : UUID.fromString(campos_transacao[1]);
                            LocalDateTime data = LocalDateTime.parse(campos_transacao[2], formatter);
                            TipoTransacao tipo = TipoTransacao.valueOf(campos_transacao[3]);
                            BigDecimal valor = new BigDecimal(campos_transacao[4]);

                            int canal_value = Integer.parseInt(campos_transacao[5]);
                            Canal canal;

                            if (canal_value == 0)
                                canal = Canal.INTERNETBAKING;
                            else if (canal_value == 1)
                                canal = Canal.CAIXA_ELETRONICO;
                            else
                                canal = Canal.CAIXA_FISICO;

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
                        BigDecimal limiteChequeEspecial = new BigDecimal(campos[10]);
                        BigDecimal taxaAdministrativa = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);
                        ContaCorrente contaCorrente = new ContaCorrente(senha, saldo, dataAbertura,
                                limiteChequeEspecial, taxaAdministrativa, nroAgencia, transacoes_conta);
                        contaCorrente.setNro_conta(nroConta); // Ajusta o UUID
                        contaCorrente.setUlt_movimentacao(ultMovimentacao);
                        contaCorrente.setSituacao(situacao);
                        return contaCorrente;
                    } else if (tipoConta == 1) { // Conta Poupança
                        BigDecimal rendimento = new BigDecimal(campos[10]);
                        int situacao = Integer.parseInt(campos[11]);

                        ContaPoupanca contaPoupanca = new ContaPoupanca(senha, saldo, dataAbertura,
                                nroAgencia, transacoes_conta);
                        contaPoupanca.setNro_conta(nroConta);
                        contaPoupanca.setUlt_movimentacao(ultMovimentacao);
                        contaPoupanca.setRendimento(rendimento);
                        contaPoupanca.setSituacao(situacao);
                        return contaPoupanca;
                    } else if (tipoConta == 2) { // Conta Salário
                        BigDecimal limiteSaque = new BigDecimal(campos[10]);
                        BigDecimal limiteTransf = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);

                        ContaSalario contaSalario = new ContaSalario(senha, saldo, dataAbertura, limiteSaque,
                                limiteTransf,
                                nroAgencia, transacoes_conta);
                        contaSalario.setNro_conta(nroConta); // Ajusta o UUID
                        contaSalario.setUlt_movimentacao(ultMovimentacao);
                        contaSalario.setSituacao(situacao);
                        return contaSalario;
                    }
                }
            }
        }

        return null;
    };

    public static Conta consultarConta(String CPF, String senha) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter();

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                String pass = campos[2].trim();
                int tipoConta = Integer.parseInt(campos[7]);
                if (cpfRegistro.equals(CPF) && pass.equals(senha)) {
                    if (tipoConta > 2)
                        return null;

                    UUID nroConta = UUID.fromString(campos[3]);
                    BigDecimal saldo = new BigDecimal(campos[4]);
                    LocalDateTime dataAbertura = LocalDateTime.parse(campos[5], formatter);
                    LocalDateTime ultMovimentacao = LocalDateTime.parse(campos[6], formatter);

                    int nroAgencia = Integer.parseInt(campos[8]);

                    List<Transacao> transacoes_conta = new ArrayList<>();
                    if (!campos[9].isEmpty()) {

                        String[] transacoes = campos[9].split("\\|");

                        for (String inf : transacoes) {
                            String[] campos_transacao = inf.split(",", -1);

                            UUID da_conta = UUID.fromString(campos_transacao[0]);
                            UUID para_conta = campos_transacao[1].isEmpty() ? null
                                    : UUID.fromString(campos_transacao[1]);
                            LocalDateTime data = LocalDateTime.parse(campos_transacao[2], formatter);
                            TipoTransacao tipo = TipoTransacao.valueOf(campos_transacao[3]);
                            BigDecimal valor = new BigDecimal(campos_transacao[4]);

                            int canal_value = Integer.parseInt(campos_transacao[5]);
                            Canal canal;

                            if (canal_value == 0)
                                canal = Canal.INTERNETBAKING;
                            else if (canal_value == 1)
                                canal = Canal.CAIXA_ELETRONICO;
                            else
                                canal = Canal.CAIXA_FISICO;

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
                        BigDecimal limiteChequeEspecial = new BigDecimal(campos[10]);
                        BigDecimal taxaAdministrativa = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);
                        ContaCorrente contaCorrente = new ContaCorrente(senha, saldo, dataAbertura,
                                limiteChequeEspecial, taxaAdministrativa, nroAgencia, transacoes_conta);
                        contaCorrente.setNro_conta(nroConta); // Ajusta o UUID
                        contaCorrente.setUlt_movimentacao(ultMovimentacao);
                        contaCorrente.setSituacao(situacao);
                        return contaCorrente;
                    } else if (tipoConta == 1) { // Conta Poupança
                        BigDecimal rendimento = new BigDecimal(campos[10]);
                        int situacao = Integer.parseInt(campos[11]);

                        ContaPoupanca contaPoupanca = new ContaPoupanca(senha, saldo, dataAbertura,
                                nroAgencia, transacoes_conta);
                        contaPoupanca.setNro_conta(nroConta);
                        contaPoupanca.setUlt_movimentacao(ultMovimentacao);
                        contaPoupanca.setRendimento(rendimento);
                        contaPoupanca.setSituacao(situacao);
                        return contaPoupanca;
                    } else if (tipoConta == 2) { // Conta Salário
                        BigDecimal limiteSaque = new BigDecimal(campos[10]);
                        BigDecimal limiteTransf = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);

                        ContaSalario contaSalario = new ContaSalario(senha, saldo, dataAbertura, limiteSaque,
                                limiteTransf,
                                nroAgencia, transacoes_conta);
                        contaSalario.setNro_conta(nroConta); // Ajusta o UUID
                        contaSalario.setUlt_movimentacao(ultMovimentacao);
                        contaSalario.setSituacao(situacao);
                        return contaSalario;
                    }
                }
            }
        }

        return null;
    };

    public static Conta consultarConta(String CPF, UUID nroConta) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter();

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                UUID nro;
                try {
                    nro = UUID.fromString(campos[3].trim());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                int tipoConta = Integer.parseInt(campos[7]);
                if (nro.equals(nroConta)) {
                    if (tipoConta > 2)
                        return null;

                    String senha = campos[2];
                    BigDecimal saldo = new BigDecimal(campos[4]);
                    LocalDateTime dataAbertura = LocalDateTime.parse(campos[5], formatter);
                    LocalDateTime ultMovimentacao = LocalDateTime.parse(campos[6], formatter);

                    int nroAgencia = Integer.parseInt(campos[8]);

                    List<Transacao> transacoes_conta = new ArrayList<>();
                    if (!campos[9].isEmpty()) {

                        String[] transacoes = campos[9].split("\\|");

                        for (String inf : transacoes) {
                            String[] campos_transacao = inf.split(",", -1);

                            UUID da_conta = UUID.fromString(campos_transacao[0]);
                            UUID para_conta = campos_transacao[1].isEmpty() ? null
                                    : UUID.fromString(campos_transacao[1]);
                            LocalDateTime data = LocalDateTime.parse(campos_transacao[2], formatter);
                            TipoTransacao tipo = TipoTransacao.valueOf(campos_transacao[3]);
                            BigDecimal valor = new BigDecimal(campos_transacao[4]);

                            int canal_value = Integer.parseInt(campos_transacao[5]);
                            Canal canal;

                            if (canal_value == 0)
                                canal = Canal.INTERNETBAKING;
                            else if (canal_value == 1)
                                canal = Canal.CAIXA_ELETRONICO;
                            else
                                canal = Canal.CAIXA_FISICO;

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
                        BigDecimal limiteChequeEspecial = new BigDecimal(campos[10]);
                        BigDecimal taxaAdministrativa = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);
                        ContaCorrente contaCorrente = new ContaCorrente(senha, saldo, dataAbertura,
                                limiteChequeEspecial, taxaAdministrativa, nroAgencia, transacoes_conta);
                        contaCorrente.setNro_conta(nroConta); // Ajusta o UUID
                        contaCorrente.setUlt_movimentacao(ultMovimentacao);
                        contaCorrente.setSituacao(situacao);
                        return contaCorrente;
                    } else if (tipoConta == 1) { // Conta Poupança
                        BigDecimal rendimento = new BigDecimal(campos[10]);
                        int situacao = Integer.parseInt(campos[11]);

                        ContaPoupanca contaPoupanca = new ContaPoupanca(senha, saldo, dataAbertura,
                                nroAgencia, transacoes_conta);
                        contaPoupanca.setNro_conta(nroConta);
                        contaPoupanca.setUlt_movimentacao(ultMovimentacao);
                        contaPoupanca.setRendimento(rendimento);
                        contaPoupanca.setSituacao(situacao);
                        return contaPoupanca;
                    } else if (tipoConta == 2) { // Conta Salário
                        BigDecimal limiteSaque = new BigDecimal(campos[10]);
                        BigDecimal limiteTransf = new BigDecimal(campos[11]);
                        int situacao = Integer.parseInt(campos[12]);

                        ContaSalario contaSalario = new ContaSalario(senha, saldo, dataAbertura, limiteSaque,
                                limiteTransf,
                                nroAgencia, transacoes_conta);
                        contaSalario.setNro_conta(nroConta); // Ajusta o UUID
                        contaSalario.setUlt_movimentacao(ultMovimentacao);
                        contaSalario.setSituacao(situacao);
                        return contaSalario;
                    }
                }
            }
        }

        return null;
    };

    // verifica se uma conta já existe no sistema. Retorna a quantidade de contas
    // com o CPF cadastrado.
    // Pode retornar 0, 1 ou 2. Um usuário pode cadastrar até duas contas.
    public static int consultarExiste(String CPF) {
        int qtd = 0;
        String inf = puxarDados();
        if (inf == null || inf.equals(".")) {
            System.out.println("\nOcorreu um erro interno.");
            return 0;
        }

        String[] blocos = inf.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF)) {
                    qtd++;
                }
            }
        }

        return qtd;
    };

    public static void salvar(Cliente cliente) {
        String inf = puxarDados();
        if (inf == null || inf.equals(".")) {
            System.out.println("\nOcorreu um erro interno.");
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
            int canal = 0;
            if (t.getCanal() == Canal.INTERNETBAKING)
                canal = 0;
            if (t.getCanal() == Canal.CAIXA_ELETRONICO)
                canal = 1;
            if (t.getCanal() == Canal.CAIXA_FISICO)
                canal = 2;

            sb.append(t.getDa_conta()).append(',')
                    .append(t.getPara_conta() != null ? t.getPara_conta() : "") // UUID para_conta ou vazio
                    .append(',').append(t.getData()) // LocalDateTime
                    .append(',').append(t.getTipo()) // TipoTransacao
                    .append(',').append(t.getValor()) // BigDecimal valor
                    .append(',').append(canal); // Canal
            if (j < conta.getHist().size() - 1) // Se for a ultima transacao para gravar, não é adicionado o marcador
                                                // ":"
                sb.append("|");
        } // Se não tiver transações, fica ...;;...;*
        sb.append(';');

        if (tipoConta == 0) {
            ContaCorrente contaCorrente = (ContaCorrente) conta;
            sb.append(contaCorrente.getLimite_cheque_especial().toString()).append(";")
                    .append(contaCorrente.getTaxa_administrativa().toString()).append(";");
        } else if (tipoConta == 1) {
            ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
            sb.append(contaPoupanca.getRendimento().toString()).append(";");
        } else {
            ContaSalario contaSalario = (ContaSalario) conta;
            sb.append(contaSalario.getLimite_saque().toString()).append(";")
                    .append(contaSalario.getLimite_transf().toString()).append(";");
        }
        sb.append(conta.getSituacao()).append("*");

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
            String[] campos = blocos.get(i).split(";");
            if (campos[3].trim().equals(cliente.getConta().getNro_conta().toString())) {
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
                System.out.println("Erro interno.");
            }
        } else {
            try (FileOutputStream file = new FileOutputStream(dbNome, true);
                    DataOutputStream arq = new DataOutputStream(file)) {
                arq.writeUTF(blocoNovo);
            } catch (IOException e) {
                System.out.println("Erro interno.");
            }
        }
    };

    public static void salvar(Conta conta) {
        String inf = puxarDados();
        if (inf == null) {
            System.out.println("Erro interno ao ler o DB");
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
                    int canal = 0;

                    if (t.getCanal() == Canal.INTERNETBAKING)
                        canal = 0;
                    if (t.getCanal() == Canal.CAIXA_ELETRONICO)
                        canal = 1;
                    if (t.getCanal() == Canal.CAIXA_FISICO)
                        canal = 2;

                    sb.append(t.getDa_conta()).append(',')
                            .append(t.getPara_conta() != null ? t.getPara_conta() : "") // UUID para_conta ou vazio
                            .append(',').append(t.getData().toString()) // LocalDateTime
                            .append(',').append(t.getTipo()) // TipoTransacao
                            .append(',').append(t.getValor()) // BigDecimal valor
                            .append(',').append(canal); // Canal
                    if (j < conta.getHist().size() - 1)
                        sb.append("|");
                }
                sb.append(';');

                switch (conta.getTipoConta()) {
                    case 0:
                        ContaCorrente cc = (ContaCorrente) conta;
                        sb.append(cc.getLimite_cheque_especial()).append(';')
                                .append(cc.getTaxa_administrativa()).append(';');
                        break;
                    case 1:
                        ContaPoupanca cp = (ContaPoupanca) conta;
                        sb.append(cp.getRendimento()).append(';');
                        break;
                    case 2:
                        ContaSalario cs = (ContaSalario) conta;
                        sb.append(cs.getLimite_saque()).append(';')
                                .append(cs.getLimite_transf()).append(';');
                        break;
                    default:

                        System.out.println("\nErro interno.");
                        return;
                }
                sb.append(conta.getSituacao());

                String novoBlocoSemAsterisco = sb.toString();
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
            System.out.println("Erro interno ao ler o DB");
            return;
        }

        int tipoFunc = func.getCargo() == "Gerente" ? 4 : 3;

        StringBuilder sb = new StringBuilder();
        sb.append(func.getCpf()).append(';') // cpf
                .append(func.getNomeCompleto()).append(';') // nome
                .append(func.getSenha()).append(';') // senha
                .append(func.getRg()).append(';') // rg
                .append(func.getDataNascimento()).append(';') // data_nasc
                .append(func.getEstadoCivil()).append(';') // estado_civil
                .append(func.getEndereco().getCidade()).append(';') // cidade
                .append(tipoFunc).append(';') // tipoConta (3 ou 4)
                .append(func.getEndereco().getEstado()).append(';') // estado
                .append(func.getEndereco().getBairro()).append(';') // bairro
                .append(func.getEndereco().getNro_local()).append(';')// nro_casa
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
            if (campos[0].trim().equals(func.getCpf())) {
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
            System.out.println("Erro interno ao escrever o DB");
        }
    }

    public static void salvar(Agencia agencia) {
        String inf = puxarDados();
        if (inf == null) {
            System.out.println("Erro interno ao ler o DB");
            return;
        }

        int tipoConta = 5;

        StringBuilder sb = new StringBuilder();
        sb.append(".").append(';')
                .append(agencia.getNome()).append(';')
                .append(agencia.getEndereco().getCidade()).append(";")
                .append(agencia.getEndereco().getBairro()).append(';')
                .append(agencia.getEndereco().getEstado()).append(';')
                .append(agencia.getEndereco().getNro_local()).append(';')
                .append(".").append(';')
                .append(tipoConta).append(';')
                .append(agencia.getNro());

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
            if (campos.length > 7 && Integer.parseInt(campos[8]) == agencia.getNro()) {
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
                System.out.println("Erro interno.");
            }
        } else {
            try (FileOutputStream file = new FileOutputStream(dbNome, true);
                    DataOutputStream arq = new DataOutputStream(file)) {
                arq.writeUTF(blocoNovo + "*");
            } catch (IOException e) {
                System.out.println("Erro interno.");
            }
        }
    }

    // Verifica se existe a agencia cadastrada.
    public static int consultarAgencia(int nro_agencia) {
        String inf = puxarDados();

        if (inf == null || inf.equals(".")) {
            System.out.println("\nOcorreu um erro interno.");
            return 0;
        }

        String[] blocos = inf.split("\\*");
        for (String bloco : blocos) {
            if (bloco.trim().isEmpty())
                continue;

            String[] campos = bloco.split(";");

            if (campos.length > 8) {

                int tipo = Integer.parseInt(campos[7].trim());
                if (tipo == 5) {

                    int nro = Integer.parseInt(campos[8].trim());
                    if (nro == nro_agencia) {
                        return 1;
                    }
                }
            }
        }

        return 0;
    }
}
