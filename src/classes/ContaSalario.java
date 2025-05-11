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

public class ContaSalario extends Conta {
    private BigDecimal limite_saque;
    private BigDecimal limite_transf;

    public ContaSalario(String senha, BigDecimal saldo, LocalDateTime data_abertura,
            BigDecimal limite_saque, BigDecimal limite_transf, int nro_agencia, List<Transacao> transacoes) {
        super(senha, saldo, nro_agencia);
        this.data_abertura = data_abertura;
        this.limite_saque = limite_saque;
        this.limite_transf = limite_transf;
        this.hist = transacoes;
        this.tipoConta = 2;
        this.situacao = 1;

    }

    public ContaSalario(String senha, int nro_agencia) {
        super(senha, nro_agencia);
        this.limite_saque = new BigDecimal("1000");
        this.limite_transf = new BigDecimal("500");
        tipoConta = 2;
        this.situacao = 1;

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

    public void deposito_transf(UUID nro_conta, BigDecimal valor, Canal canal) {
        this.saldo = this.saldo.add(valor);
        this.ult_movimentacao = LocalDateTime.now();

        Transacao transacao = new Transacao(nro_conta, this.nro_conta, LocalDateTime.now(), TipoTransacao.TRANSFERENCIA,
                valor, canal);
        hist.add(transacao);
    }

    @Override
    public void deposito_pagamento(UUID nro_conta, BigDecimal valor, Canal canal) {
        // Não faz pagamento.
    }

    @Override
    public void depositar(BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.add(valor);
            this.ult_movimentacao = LocalDateTime.now();
            Transacao transacao = new Transacao(nro_conta, LocalDateTime.now(), TipoTransacao.DEPOSITO, valor, canal);
            hist.add(transacao);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            throw new SaldoException(SaldoCode.DEPOSITO_NEGATIVO.getMsg());
            // System.out.println("Valor inválido para depósito.");
        }
    }

    @Override
    public void sacar(BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 &&
                valor.compareTo(this.limite_saque) <= 0 &&
                valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();

            Transacao transacao = new Transacao(nro_conta, LocalDateTime.now(), TipoTransacao.SAQUE, valor, canal);
            hist.add(transacao);
        } else {
            throw new SaldoException(SaldoCode.SAQUE_NEGATIVO.getMsg());
        }
    }

    @Override
    public void transferir(String cpf_destino, int tipo, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 &&
                valor.compareTo(this.limite_transf) <= 0 &&
                valor.compareTo(this.saldo) <= 0) {
            Conta dest = MetodosDB.consultarConta(cpf_destino, tipo);
            if (dest == null) {
                System.out.println("Não é possível fazer pagamento para esse cliente.");
                return;
            }

            this.saldo = this.saldo.subtract(valor);

            dest.deposito_transf(nro_conta, valor, canal);
            MetodosDB.salvar(dest);

            this.ult_movimentacao = LocalDateTime.now();

            Transacao transacao = new Transacao(nro_conta, dest.getNro_conta(), LocalDateTime.now(),
                    TipoTransacao.TRANSFERENCIA, valor, canal);

            hist.add(transacao);
        } else {
            throw new SaldoException(SaldoCode.SAQUE_NEGATIVO.getMsg());
        }
    }

    @Override
    public void efetuarPagamento(String cpf_destino, BigDecimal valor, Canal canal) throws SaldoException {
        // Não realiza pagamento.
    }

    @Override
    public void consultarSaldo() {

        System.out.println("Saldo atual: " + formatoMonetarioBrasileiro(this.saldo));
        System.out.println("Limite de saque: " + formatoMonetarioBrasileiro(this.limite_saque));
        System.out.println("Limite de transferência: " + formatoMonetarioBrasileiro(this.limite_transf));
    }
}
