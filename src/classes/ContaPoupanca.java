package classes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import enums.Canal;
import enums.SaldoCode;
import enums.TipoTransacao;
import exceptions.SaldoException;

public class ContaPoupanca extends Conta {
    private BigDecimal rendimento;

    public ContaPoupanca(String senha, int nro_conta, BigDecimal saldo, LocalDateTime data_abertura) {
        super(senha, nro_conta, saldo, data_abertura);
        this.rendimento = BigDecimal.ZERO;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
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
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            throw new SaldoException(SaldoCode.SAQUE_NEGATIVO.getMsg());
            // System.out.println("Valor de saque inválido.");
        }
    }

    @Override
    public void transferir(Conta conta_destino, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
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

    public void calcularRendimento() {
        // Supondo uma taxa de rendimento de 0.5% ao mês
        BigDecimal taxaRendimento = new BigDecimal("0.005");
        this.rendimento = this.saldo.multiply(taxaRendimento);
        this.saldo = this.saldo.add(this.rendimento);
        this.ult_movimentacao = LocalDateTime.now();
    }

    public void aplicarRendimento() {
        this.saldo = this.saldo.add(this.rendimento);
        this.rendimento = BigDecimal.ZERO;
        this.ult_movimentacao = LocalDateTime.now();
    }

    @Override
    public void consultarSaldo(String saldo) {
        System.out.println("Saldo atual: " + this.saldo);
        System.out.println("Rendimento acumulado: " + this.rendimento);
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
