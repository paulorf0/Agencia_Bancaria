package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public void depositar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            this.saldo = this.saldo.add(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor de depósito inválido.");
        }
    }

    @Override
    public void sacar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor de saque inválido ou saldo insuficiente.");
        }
    }

    @Override
    public void transferir(Conta conta_destino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            conta_destino.depositar(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor de transferência inválido ou saldo insuficiente.");
        }
    }

    @Override
    public void efetuarPagamento(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor de pagamento inválido ou saldo insuficiente.");
        }
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

    @Override
    public void consultarSaldo(String saldo) {
        System.out.println("Saldo atual: R$ " + this.saldo);
        System.out.println("Rendimento acumulado: R$ " + this.rendimento);
    }
}
