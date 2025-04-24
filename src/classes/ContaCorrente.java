package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import enums.Canal;
import enums.SaldoCode;
import enums.TipoTransacao;
import exceptions.SaldoException;
import outros.MetodosDB;

public class ContaCorrente extends Conta {
    private BigDecimal limite_cheque_especial;
    private BigDecimal taxa_administrativa;

    public ContaCorrente(String senha, BigDecimal saldo, LocalDateTime data_abertura,
            BigDecimal limite_cheque_especial, BigDecimal taxa_administrativa, int nro_agencia,
            List<Transacao> transacoes) {
        super(senha, saldo, nro_agencia);
        this.data_abertura = data_abertura;
        this.limite_cheque_especial = limite_cheque_especial;
        this.taxa_administrativa = taxa_administrativa;
        this.tipoConta = 0;
        this.hist = transacoes;
        this.situacao = 1;
    }

    public ContaCorrente(String senha, int nro_agencia) {
        super(senha, nro_agencia);
        this.limite_cheque_especial = new BigDecimal("500.00"); // valor padrão
        this.taxa_administrativa = new BigDecimal("10.00"); // valor padrão
        tipoConta = 0;
        this.situacao = 1;

    }

    public BigDecimal getLimite_cheque_especial() {
        return limite_cheque_especial;
    }

    public void setLimite_cheque_especial(BigDecimal limite_cheque_especial) {
        this.limite_cheque_especial = limite_cheque_especial;
    }

    public BigDecimal getTaxa_administrativa() {
        return taxa_administrativa;
    }

    public void setTaxa_administrativa(BigDecimal taxa_administrativa) {
        this.taxa_administrativa = taxa_administrativa;
    }

    public void deposito_transf(UUID nro_conta, BigDecimal valor, Canal canal) {
        this.saldo = this.saldo.add(valor);
        this.ult_movimentacao = LocalDateTime.now();

        Transacao transacao = new Transacao(nro_conta, this.nro_conta, LocalDateTime.now(), TipoTransacao.TRANSFERENCIA,
                valor, canal);
        hist.add(transacao);
    }

    @Override
    public void deposito_pagamento(UUID nro_conta, BigDecimal valor, Canal canal) {
        this.saldo = this.saldo.add(valor);
        this.ult_movimentacao = LocalDateTime.now();

        Transacao transacao = new Transacao(nro_conta, this.nro_conta, LocalDateTime.now(), TipoTransacao.PAGAMENTO,
                valor, canal);
        hist.add(transacao);
    }

    @Override
    public void depositar(BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.add(valor);

            Transacao transacao = new Transacao(nro_conta, LocalDateTime.now(), TipoTransacao.DEPOSITO, valor, canal);
            hist.add(transacao);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para depósito.");
            throw new SaldoException(SaldoCode.DEPOSITO_NEGATIVO.getMsg());
            // System.out.println("Valor inválido para depósito.");
        }
    }

    @Override
    public void sacar(BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();

            Transacao transacao = new Transacao(nro_conta, LocalDateTime.now(), TipoTransacao.SAQUE, valor, canal);
            hist.add(transacao);
        } else {
            System.out.println("Valor inválido para saque ou saldo insuficiente.");
            throw new SaldoException(SaldoCode.SAQUE_NEGATIVO.getMsg());
        }
    }

    @Override
    public void transferir(String cpf_destino, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
            this.saldo = this.saldo.subtract(valor);
            Conta dest = MetodosDB.consultarConta(cpf_destino);

            dest.deposito_transf(nro_conta, valor, canal);
            MetodosDB.salvar(dest);

            this.ult_movimentacao = LocalDateTime.now();

            Transacao transacao = new Transacao(nro_conta, dest.getNro_conta(), LocalDateTime.now(),
                    TipoTransacao.TRANSFERENCIA, valor, canal);

            hist.add(transacao);
        } else {
            System.out.println("Valor inválido para transferência ou saldo insuficiente.");
        }
    }

    @Override
    public void efetuarPagamento(String cpf_destino, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
            this.ult_movimentacao = LocalDateTime.now();

            if (MetodosDB.consultarExiste(cpf_destino) == 0) {
                outros.Utils.limparConsole();
                System.out.println("CPF inexistente no sistema.");
                return;
            }

            Conta dest = MetodosDB.consultarConta(cpf_destino);
            if (dest == null) {
                System.out.println("O CPF pertence a um funcionario");
                return;
            }

            dest.deposito_pagamento(nro_conta, valor, canal);
            MetodosDB.salvar(dest);

            Transacao transacao = new Transacao(this.nro_conta, dest.getNro_conta(), LocalDateTime.now(),
                    TipoTransacao.PAGAMENTO, valor, canal);

            hist.add(transacao);
        } else {
            System.out.println("Valor inválido para pagamento ou saldo insuficiente.");
        }
    }

    @Override
    public void consultarSaldo() {
        System.out.println("Saldo atual: " + formatoMonetarioBrasileiro(this.saldo));
        System.out.println("Limite cheque especial: " + formatoMonetarioBrasileiro(this.limite_cheque_especial));
        System.out.println("Taxa administrativa: " + formatoMonetarioBrasileiro(this.taxa_administrativa));
    }
}
