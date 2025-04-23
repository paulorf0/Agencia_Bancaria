package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import enums.Canal;
import exceptions.SaldoException;

public abstract class Conta {
    protected String senha;
    protected UUID nro_conta;
    protected BigDecimal saldo;
    protected List<Transacao> hist;
    protected LocalDateTime ult_movimentacao;
    protected LocalDateTime data_abertura;
    protected int tipoConta; // 0 se corrente, 1 se poupança, 2 se salario
    protected int nro_agencia;

    public Conta(String senha, BigDecimal saldo, int nro_agencia) {
        this.senha = senha;
        this.saldo = saldo;
        this.ult_movimentacao = data_abertura;
        this.nro_agencia = nro_agencia;
        this.nro_conta = UUID.randomUUID();
        this.hist = new ArrayList<>();
        this.data_abertura = LocalDateTime.now();
    }

    public Conta(String senha, int nro_agencia) {
        this.senha = senha;
        this.nro_agencia = nro_agencia;

        this.data_abertura = LocalDateTime.now();
        this.ult_movimentacao = data_abertura;
        this.hist = new ArrayList<>();
        this.nro_conta = UUID.randomUUID();
        this.saldo = BigDecimal.ZERO;
    }

    public Conta() {
        this.nro_conta = UUID.randomUUID();
        this.data_abertura = LocalDateTime.now();
        this.hist = new ArrayList<>();
    }

    public abstract void depositar(BigDecimal valor, Canal canal) throws SaldoException;

    // Deposito vindo de outra pessoa.
    public abstract void deposito_transf(UUID nro_conta, BigDecimal valor, Canal canal);

    public abstract void deposito_pagamento(UUID nro_conta, BigDecimal valor, Canal canal);

    public abstract void sacar(BigDecimal valor, Canal canal) throws SaldoException;

    public abstract void transferir(String cpf_destino, BigDecimal valor, Canal canal) throws SaldoException;

    public abstract void efetuarPagamento(String cpf_destino, BigDecimal valor, Canal canal) throws SaldoException;

    public abstract void consultarSaldo();

    public String consultarInf() {
        return "Número da Conta: " + nro_conta +
                "\nSaldo: " + saldo +
                "\nÚltima Movimentação: " + ult_movimentacao +
                "\nData de Abertura: " + data_abertura +
                "\nTipo de Conta: " + (tipoConta == 0 ? "Corrente" : tipoConta == 1 ? "Poupança" : "Salário") +
                "\nNúmero da Agência: " + nro_agencia;
    }

    public String consultarHist() {
        StringBuilder historico = new StringBuilder();

        for (Transacao transacao : hist) {
            String canal_string = "";
            if (transacao.getCanal().equals(Canal.CAIXA_ELETRONICO))
                canal_string = "Caixa eletronico";
            if (transacao.getCanal().equals(Canal.INTERNETBAKING))
                canal_string = "Internet Baking";
            if (transacao.getCanal().equals(Canal.CAIXA_FISICO))
                canal_string = "Caixa Fisico";

            historico.append("Data: ").append(transacao.getData()).append("\n")
                    .append("Tipo: ").append(transacao.getTipo()).append("\n")
                    .append("Canal: ").append(canal_string).append("\n")
                    .append("Valor: ").append(transacao.getValor()).append("\n\n");
        }

        return historico.toString();
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UUID getNro_conta() {
        return nro_conta;
    }

    public void setNro_conta(UUID nro_conta) {
        this.nro_conta = nro_conta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getUlt_movimentacao() {
        return ult_movimentacao;
    }

    public void setUlt_movimentacao(LocalDateTime ult_movimentacao) {
        this.ult_movimentacao = ult_movimentacao;
    }

    public LocalDateTime getData_abertura() {
        return data_abertura;
    }

    public void setData_abertura(LocalDateTime data_abertura) {
        this.data_abertura = data_abertura;
    }

    public int getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(int tipoConta) {
        this.tipoConta = tipoConta;
    }

    public int getNro_agencia() {
        return nro_agencia;
    }

    public void setNro_agencia(int nro_agencia) {
        this.nro_agencia = nro_agencia;
    }

    public List<Transacao> getHist() {
        return hist;
    }

}
