package classes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import enums.Canal;
import enums.SaldoCode;
import enums.TipoTransacao;
import exceptions.SaldoException;

public class ContaSalario extends Conta {
    private BigDecimal limite_saque;
    private BigDecimal limite_transf;

    public ContaSalario(String senha, int nro_conta, BigDecimal saldo, LocalDateTime data_abertura,
            BigDecimal limite_saque, BigDecimal limite_transf) {
        super(senha, nro_conta, saldo, data_abertura);
        this.limite_saque = limite_saque;
        this.limite_transf = limite_transf;
    }

    public BigDecimal getLimite_saque() {
        return limite_saque;
    }

    public void setLimite_saque(BigDecimal limite_saque) {
        this.limite_saque = limite_saque;
    }

    public BigDecimal getLimite_transf() {
        return limite_transf;
    }

    public void setLimite_transf(BigDecimal limite_transf) {
        this.limite_transf = limite_transf;
    }

    @Override
    public void depositar(BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.add(valor);
            this.ult_movimentacao = LocalDate.now().atStartOfDay();

            Transacao transacao = new Transacao(nro_conta, LocalDateTime.now(), TipoTransacao.DEPOSITO, valor, canal);
            hist.add(transacao);
        } else {
            throw new SaldoException(SaldoCode.DEPOSITO_NEGATIVO.getMsg());
            // System.out.println("Valor inválido para depósito.");
        }
    }

    @Override
    public void sacar(BigDecimal valor) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.limite_saque) <= 0
                && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            throw new SaldoException(SaldoCode.SAQUE_NEGATIVO.getMsg());

            // System.out.println("Valor inválido para saque.");
        }
    }

    @Override
    public void transferir(Conta conta_destino, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.limite_transf) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            conta_destino.deposito_transf(nro_conta, valor, canal);
            this.ult_movimentacao = LocalDate.now().atStartOfDay();

            Transacao transacao = new Transacao(nro_conta, conta_destino.getNro_conta(), LocalDateTime.now(),
                    TipoTransacao.TRANSFERENCIA, valor, canal);

            hist.add(transacao);
        } else {
            throw new SaldoException(SaldoCode.TRANSFERENCIA_NEGATIVA.getMsg());
            // System.out.println("Valor inválido para transferência ou saldo
            // insuficiente.");
        }
    }

    @Override
    public void efetuarPagamento(Conta conta_destino, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDate.now().atStartOfDay();
            conta_destino.deposito_pagamento(this.nro_conta, valor, canal);

            Transacao transacao = new Transacao(this.nro_conta, conta_destino.getNro_conta(), LocalDateTime.now(),
                    TipoTransacao.PAGAMENTO, valor, canal);

            hist.add(transacao);
        } else {
            throw new SaldoException(SaldoCode.TRANSFERENCIA_NEGATIVA.getMsg());
            // System.out.println("Valor inválido para pagamento ou saldo insuficiente.");
        }
    }


    @Override
    public void consultarSaldo(String saldo) {
        System.out.println("Saldo atual: " + this.saldo);
    }

    @Override
    public void deposito_transf(int nro_conta, BigDecimal valor, Canal canal) {
        this.saldo = this.saldo.add(valor);
        this.ult_movimentacao = LocalDate.now().atStartOfDay();

        Transacao transacao = new Transacao(nro_conta, this.nro_conta, LocalDateTime.now(), TipoTransacao.TRANSFERENCIA,
                valor, canal);
        hist.add(transacao);
    }

    @Override
    public void deposito_pagamento(int nro_conta, BigDecimal valor, Canal canal) {
        this.saldo = this.saldo.add(valor);
        this.ult_movimentacao = LocalDate.now().atStartOfDay();

        Transacao transacao = new Transacao(nro_conta, this.nro_conta, LocalDateTime.now(), TipoTransacao.PAGAMENTO,
                valor, canal);
        hist.add(transacao);
    }

}
