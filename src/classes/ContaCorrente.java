package classes;

import java.time.LocalDate;
import java.time.LocalDateTime;

import enums.Canal;
import enums.SaldoCode;
import enums.TipoTransacao;
import exceptions.SaldoException;

import java.math.BigDecimal;

public class ContaCorrente extends Conta {
    private BigDecimal limite_cheque_especial;
    private BigDecimal taxa_administrativa;

    public ContaCorrente(String senha, int nro_conta, BigDecimal saldo, LocalDate data_abertura,
            BigDecimal limite_cheque_especial, BigDecimal taxa_administrativa) {
        super(senha, nro_conta, saldo, data_abertura.atStartOfDay());
        this.limite_cheque_especial = limite_cheque_especial;
        this.taxa_administrativa = taxa_administrativa;
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
        if (valor.compareTo(BigDecimal.ZERO) > 0 && this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDate.now().atStartOfDay();
        } else {
            throw new SaldoException(SaldoCode.SAQUE_NEGATIVO.getMsg());

            // System.out.println("Valor inválido para saque ou saldo insuficiente.");
        }
    }

    @Override
    public void transferir(Conta conta_destino, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
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
        if (valor.compareTo(BigDecimal.ZERO) > 0 && this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
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
        System.out.println("Limite cheque especial: " + this.limite_cheque_especial);
        System.out.println("Taxa administrativa: " + this.taxa_administrativa);
    }

}
