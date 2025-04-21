package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Conta {
    protected String senha;
    protected UUID nro_conta;
    protected BigDecimal saldo;
    protected LocalDateTime ult_movimentacao;
    protected LocalDateTime data_abertura;
    protected int tipoConta; // 0 se corrente, 1 se poupança, 2 se salario
    protected UUID nro_agencia;

    public Conta(String senha, BigDecimal saldo, LocalDateTime data_abertura, UUID nro_agencia) {
        this.senha = senha;
        this.saldo = saldo;
        this.data_abertura = data_abertura;
        this.ult_movimentacao = data_abertura;
        this.nro_agencia = nro_agencia;
        this.nro_conta = UUID.randomUUID();
    }

    public Conta() {
        this.nro_conta = UUID.randomUUID();
        this.data_abertura = LocalDateTime.now();
    }

    public abstract void depositar(BigDecimal valor);

    public abstract void sacar(BigDecimal valor);

    public abstract void transferir(Conta conta_destino, BigDecimal valor);

    public abstract void efetuarPagamento(BigDecimal valor);

    public abstract void consultarSaldo(String saldo);

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

    public UUID getNro_agencia() {
        return nro_agencia;
    }

    public void setNro_agencia(UUID nro_agencia) {
        this.nro_agencia = nro_agencia;
    }

}
