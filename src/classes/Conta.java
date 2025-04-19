package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Conta {
    protected String senha;
    protected int nro_conta;
    protected BigDecimal saldo;
    protected LocalDateTime data_abertura;
    protected LocalDateTime ult_movimentacao;

    public Conta(String senha, int nro_conta, BigDecimal saldo, LocalDateTime data_abertura) {
        this.senha = senha;
        this.nro_conta = nro_conta;
        this.saldo = saldo;
        this.data_abertura = data_abertura;
        this.ult_movimentacao = data_abertura;
    }

    public Conta() {

    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public int getNro_conta() {
        return nro_conta;
    }
    public void setNro_conta(int nro_conta) {
        this.nro_conta = nro_conta;
    }
    public BigDecimal getSaldo() {
        return saldo;
    }
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
    public LocalDateTime getData_abertura() {
        return data_abertura;
    }
    public void setData_abertura(LocalDateTime data_abertura) {
        this.data_abertura = data_abertura;
    }
    public LocalDateTime getUlt_movimentacao() {
        return ult_movimentacao;
    }
    public void setUlt_movimentacao(LocalDateTime ult_movimentacao) {
        this.ult_movimentacao = ult_movimentacao;
    }
    public abstract void depositar(BigDecimal valor);
    public abstract void sacar(BigDecimal valor);
    public abstract void transferir(Conta conta_destino, BigDecimal valor);
    public abstract void efetuarPagamento(BigDecimal valor);
    public abstract void consultarSaldo(String saldo);
}
