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

    // SR = Sem Restrição. Por ex: Informação de funcionário.
    private static List<String> buscar_info_SR(String CPF, int index) {
        String dados = puxarDados();
        if (dados == null)
            return null;

        List<String> dado = new ArrayList<>();
        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                if (cpfRegistro.equals(CPF))
                    dado.add(campos[index]);
            }
        }

        return dado;
    }

    // CR = Com restrição (apenas para contas)
    private static List<String> buscar_info_CR_conta(String CPF, int index) {
        String dados = puxarDados();
        List<String> dado = new ArrayList<>();
        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                int tipoConta = Integer.parseInt(campos[7].trim());
                if (cpfRegistro.equals(CPF))
                    if (tipoConta < 3)
                        dado.add(campos[index]);
                    else
                        dado.add("null");
            }
        }

        return dado;
    }

    // CR = Com restrição (apenas para contas)
    private static String buscar_info_CR_nro(UUID nroC, int index) {
        String dados = puxarDados();
        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String nro = campos[3].trim();
                if (nro.equals(nroC.toString()))
                    return campos[index];

            }
        }

        return null;
    }

    // Busca o bloco de informações do funcionário utilizando o CPF.
    private static String bloco_func(String CPF) {
        String dados = puxarDados();
        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");
        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0 && campos[0].trim().equals(CPF))
                return bloco;
        }

        return null;
    }

    private static String bloco_cliente_tipo(String CPF, int tipoC) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                int tipoConta = Integer.parseInt(campos[7]);
                if (cpfRegistro.equals(CPF) && tipoConta == tipoC)
                    if (tipoConta > 2)
                        return null;
                    else
                        return bloco;
            }
        }

        return null;
    }

    private static String bloco_cliente_cpf(String CPF) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                int tipoConta = Integer.parseInt(campos[7]);
                if (cpfRegistro.equals(CPF))
                    if (tipoConta > 2)
                        return null;
                    else
                        return bloco;
            }
        }

        return null;
    }

    private static String bloco_cliente_nro(UUID nroC) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

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
                if (nro.equals(nroC))
                    if (tipoConta > 2)
                        return null;
                    else
                        return bloco;
            }
        }

        return null;
    }

    private static String bloco_cliente_senha(String CPF, String senha) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");

        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                int tipoConta = Integer.parseInt(campos[7]);
                String pass = campos[2].trim();
                if (cpfRegistro.equals(CPF) && pass.equals(senha))
                    if (tipoConta > 2)
                        return null;
                    else
                        return bloco;
            }
        }

        return null;
    }

    private static Conta string_to_conta(String[] campos) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter();

        int tipoConta = Integer.parseInt(campos[7]);
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

        return null;
    }

    // não tem info de CPF e NOME.
    private static StringBuilder conta_to_string(Conta conta) {

        StringBuilder sb = new StringBuilder();
        sb.append(conta.getSenha()).append(';')
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
                return null;
        }
        sb.append(conta.getSituacao());

        return sb; // Sem o * de terminação.
    }

    public static StringBuilder func_to_string(Funcionario func) {
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
        return sb;
    }

    private static int idxBlocoConta(UUID nroConta) {
        List<String> blocos = infoBlocoList();

        for (int i = 0; i < blocos.size(); i++) {
            String[] campos = blocos.get(i).split(";");
            if (campos.length > 3 && campos[3].trim().equals(nroConta.toString()))
                return i;
        }

        return -1;
    }

    private static int idxBlocoCPF(String CPF) {
        List<String> blocos = infoBlocoList();

        for (int i = 0; i < blocos.size(); i++) {
            String[] campos = blocos.get(i).split(";");
            if (campos.length > 3 && campos[0].trim().equals(CPF))
                return i;
        }

        return -1;
    }

    public static String[] infoBlocoArray() {
        String dado = puxarDados();
        if (dado == null)
            return null;
        return dado.split(";");
    }

    public static List<String> infoBlocoList() {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] linha = dados.split("\\*");
        List<String> blocos = new ArrayList<>();
        for (String bloco : linha) {
            if (!bloco.trim().isEmpty()) {
                blocos.add(bloco);
            }
        }

        return blocos;
    }

    public static void salvarBloco(List<String> bloco) {
        int tentativas = 0;
        boolean sucesso = false;
        while (tentativas < 5 && !sucesso) {
            try (FileOutputStream file = new FileOutputStream(dbNome);
                    DataOutputStream arq = new DataOutputStream(file)) {
                for (String b : bloco) {
                    arq.writeUTF(b + "*");
                }
                sucesso = true;
            } catch (IOException e) {
                tentativas++;
                if (tentativas >= 5) {
                    System.out.println("\nErro interno grave.");
                }
            }
        }
    }

    public static void salvarBloco(String[] bloco) {
        int tentativas = 0;
        boolean sucesso = false;
        while (tentativas < 5 && !sucesso) {
            try (FileOutputStream file = new FileOutputStream(dbNome);
                    DataOutputStream arq = new DataOutputStream(file)) {
                for (String b : bloco) {
                    arq.writeUTF(b + "*");
                }
                sucesso = true;
            } catch (IOException e) {
                tentativas++;
                if (tentativas >= 5) {
                    System.out.println("\nErro interno grave.");
                }
            }
        }
    }

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
        List<String> tiposString = MetodosDB.buscar_info_SR(CPF, 7);
        if (tiposString == null)
            return null;

        List<Integer> tiposInt = tiposString.stream()
                .map(el -> Integer.parseInt(el))
                .toList();

        return tiposInt;
    }

    public static List<String> consultarSenha(String CPF) {
        return buscar_info_SR(CPF, 2);
    }

    public static List<String> consultarNroConta(String CPF) {
        return buscar_info_CR_conta(CPF, 03);
    }

    public static String consultarNroConta(String CPF, int tipo) {
        String dados = puxarDados();

        if (dados == null)
            return null;

        String[] blocos = dados.split("\\*");
        for (String bloco : blocos) {
            String[] campos = bloco.split(";");
            if (campos.length > 0) {
                String cpfRegistro = campos[0].trim();
                int tipoConta = Integer.parseInt(campos[7].trim());
                if (cpfRegistro.equals(CPF) && tipoConta == tipo)
                    // FOI ALTERADO E NÃO TESTADO AINDA.
                    if (tipoConta < 3)
                        return campos[3];
                    else
                        return null;
            }
        }

        return null;
    }

    public static Funcionario consultarFuncionario(String CPF) {

        String bloco = bloco_func(CPF);
        String[] campos = bloco.split(";");

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        return null;
    }

    // Buscar uma conta pelo tipo de conta e CPF.
    public static Conta consultarConta(String CPF, int tipoC) {
        String bloco = bloco_cliente_tipo(CPF, tipoC);
        String[] campos = bloco.split(";");

        return string_to_conta(campos);
    };

    public static Conta consultarConta(String CPF) {
        String bloco = bloco_cliente_cpf(CPF);
        String[] campos = bloco.split(";");

        return string_to_conta(campos);
    };

    public static Conta consultarConta(String CPF, String senha) {
        String bloco = bloco_cliente_senha(CPF, senha);
        String[] campos = bloco.split(";");

        return string_to_conta(campos);
    }

    public static Conta consultarConta(String CPF, UUID nroConta) {
        String bloco = bloco_cliente_nro(nroConta);
        String[] campos = bloco.split(";");

        return string_to_conta(campos);
    };

    public static int consultarExiste(String CPF) {
        // Essa função retorna quantas contas um usuário possuí cadastrada.
        // Consequentemente, se o CPF tem cadastro no sistema.
        // Para economizar código, apenas busca a informação do CPF reutilizando o
        // método buscar_info_SR e retorno a quantidade de vezes que o CPF foi
        // encontrado no sistema.

        List<String> inf = buscar_info_SR(CPF, 0);
        return inf.size();
    };

    public static void salvar(Cliente cliente) {
        String cpf = cliente.getNome();
        String nome = cliente.getCpf();

        Conta conta = cliente.getConta();
        StringBuilder sb = conta_to_string(conta);
        sb.insert(0, nome + ";");
        sb.insert(0, cpf + ";");
        String blocoNovo = sb.toString(); // Esse bloco contém as informações da conta.

        int idx = idxBlocoConta(conta.getNro_conta());
        if (idx == -1) {
            // Cliente novo no sistema.

            try (FileOutputStream file = new FileOutputStream(dbNome, true);
                    DataOutputStream arq = new DataOutputStream(file)) {
                arq.writeUTF(blocoNovo);
            } catch (IOException e) {
                System.out.println("Erro interno grave.");
            }

            return;
        }

        List<String> blocos = infoBlocoList();
        if (blocos == null) {
            Utils.limparConsole();
            System.out.println("\nErro interno.");
            return;
        }
        blocos.set(idx, blocoNovo);

        salvarBloco(blocos);
    };

    public static void salvar(Conta conta) {
        String cpf = buscar_info_CR_nro(conta.getNro_conta(), 0);
        String nome = buscar_info_CR_nro(conta.getNro_conta(), 1);

        // PODE OCORRER ALGUM ERRO. SE ALGUM ERRO OCORRER, PODE SER ESSE TRECHO.
        StringBuilder sb = conta_to_string(conta);
        sb.insert(0, nome + ";");
        sb.insert(0, cpf + ";");
        String novoBloco = sb.toString();

        if (novoBloco == null) {
            Utils.limparConsole();
            System.out.println("\nErro interno.");
            return;
        }
        int idx = idxBlocoConta(conta.getNro_conta());
        if (idx == -1) {
            Utils.limparConsole();
            System.out.println("\nErro interno.");
            return;
        }

        List<String> blocos = infoBlocoList();
        if (blocos == null) {
            Utils.limparConsole();
            System.out.println("\nErro interno.");
            return;
        }

        blocos.set(idx, novoBloco);
        salvarBloco(blocos);
    }

    public static void salvar(Funcionario func) {

        String blocoNovo = func_to_string(func).toString();

        int idx = idxBlocoCPF(func.getCpf());
        if (idx == -1) {
            // Cliente novo no sistema.

            try (FileOutputStream file = new FileOutputStream(dbNome, true);
                    DataOutputStream arq = new DataOutputStream(file)) {
                arq.writeUTF(blocoNovo);
            } catch (IOException e) {
                System.out.println("Erro interno grave.");
            }

            return;
        }

        List<String> blocos = infoBlocoList();
        if (blocos == null) {
            Utils.limparConsole();
            System.out.println("\nErro interno.");
            return;
        }
        blocos.set(idx, blocoNovo);

        salvarBloco(blocos);
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

        List<String> blocos = infoBlocoList();
        if (blocos == null) {
            Utils.limparConsole();
            System.out.println("\nErro interno.");
            return;
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
        String[] blocos = infoBlocoArray();
        if (blocos == null) {
            System.out.println("Erro interno.");
            return 0;
        }
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
