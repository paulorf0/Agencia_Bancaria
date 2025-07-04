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

public class ContaPoupanca extends Conta {
    private BigDecimal rendimento;

    public ContaPoupanca(String senha, BigDecimal saldo, LocalDateTime data_abertura, int nro_agencia,
            List<Transacao> transacoes) {
        super(senha, saldo, nro_agencia);
        this.data_abertura = data_abertura;
        this.rendimento = BigDecimal.ZERO;
        this.hist = transacoes;
        this.tipoConta = 1;
        this.situacao = 1;

    }

    public ContaPoupanca(String senha, int nro_agencia) {
        super(senha, nro_agencia);
        this.rendimento = BigDecimal.ZERO;
        this.tipoConta = 1;
        this.situacao = 1;

    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
    }

    public void calcularRendimento() {
        // Supondo uma taxa de rendimento de 0.5% ao mês
        BigDecimal taxaRendimento = new BigDecimal("0.005");
        this.rendimento = this.saldo.multiply(taxaRendimento);
        this.ult_movimentacao = LocalDateTime.now();
    }

    public void aplicarRendimento() {
        this.saldo = this.saldo.add(this.rendimento);
        this.rendimento = BigDecimal.ZERO;
        this.ult_movimentacao = LocalDateTime.now();
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
       // Não recebe pagamento
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
            System.out.println("Valor inválido para depósito.");
            throw new SaldoException(SaldoCode.DEPOSITO_NEGATIVO.getMsg());
            // System.out.println("Valor inválido para depósito.");
        }
    }

    @Override
    public void sacar(BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
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
    public void transferir(String cpf_destino, int tipo, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            Conta dest = MetodosDB.consultarConta(cpf_destino, tipo);
            if (dest == null) {
                System.out.println("Não é possível fazer transferência para esse cliente.");
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
            System.out.println("Valor inválido para transferência ou saldo insuficiente.");
        }
    }

    @Override
    public void transferir(String cpf_destino, UUID nro, BigDecimal valor, Canal canal) throws SaldoException {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            Conta dest = MetodosDB.consultarConta(cpf_destino, nro);
            if (dest == null) {
                System.out.println("Não é possível fazer transferência para esse cliente.");
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
            System.out.println("Valor inválido para transferência ou saldo insuficiente.");
        }
    }

    @Override
    public void efetuarPagamento(String cpf_destino, UUID nro, BigDecimal valor, Canal canal) throws SaldoException {
        // Não efetua pagamento.
    }

    @Override
    public void consultarSaldo() {
        System.out.println("Saldo atual: " + formatoMonetarioBrasileiro(this.saldo));
        System.out.println("Rendimento acumulado: " + formatoMonetarioBrasileiro(this.rendimento));
    }
}
