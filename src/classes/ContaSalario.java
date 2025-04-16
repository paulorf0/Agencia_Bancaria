package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaSalario extends Conta {
    private BigDecimal limite_saque;
    private BigDecimal limite_transf;

    public ContaSalario(String senha, int nro_conta, BigDecimal saldo, LocalDateTime data_abertura, BigDecimal limite_saque, BigDecimal limite_transf) {
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
    public void depositar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.add(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para depósito.");
        }
    }
    @Override
    public void sacar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.limite_saque) <= 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para saque.");
        }
    }
    @Override
    public void transferir(Conta conta_destino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.limite_transf) <= 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            conta_destino.depositar(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para transferência.");
        }
    }
    @Override
    public void efetuarPagamento(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para pagamento.");
        }
    }
    @Override
    public void consultarSaldo(String saldo) {
        System.out.println("Saldo atual: " + this.saldo);
    }

}
