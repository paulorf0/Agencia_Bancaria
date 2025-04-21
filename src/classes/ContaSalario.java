package classes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ContaSalario extends Conta {
    private BigDecimal limite_saque;
    private BigDecimal limite_transf;

    public ContaSalario(String senha, int nro_conta, BigDecimal saldo, LocalDateTime data_abertura,
            BigDecimal limite_saque, BigDecimal limite_transf, UUID nro_agencia) {
        super(senha, saldo, data_abertura, nro_agencia);
        this.limite_saque = limite_saque;
        this.limite_transf = limite_transf;
        this.tipoConta = 2;
    }

    public ContaSalario(String senha, int nroConta, BigDecimal saldo, LocalDateTime dataAbertura, UUID nro_agencia) {
        super(senha, saldo, dataAbertura, nro_agencia);
        this.limite_saque = new BigDecimal("1000");
        this.limite_transf = new BigDecimal("500");
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
        if (valor.compareTo(BigDecimal.ZERO) > 0 &&
                valor.compareTo(this.limite_saque) <= 0 &&
                valor.compareTo(this.saldo) <= 0) {

            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor de saque inválido ou limite/saldo insuficiente.");
        }
    }

    @Override
    public void transferir(Conta conta_destino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 &&
                valor.compareTo(this.limite_transf) <= 0 &&
                valor.compareTo(this.saldo) <= 0) {

            this.saldo = this.saldo.subtract(valor);
            conta_destino.depositar(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor de transferência inválido ou limite/saldo insuficiente.");
        }
    }

    @Override
    public void efetuarPagamento(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 && valor.compareTo(this.saldo) <= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para pagamento ou saldo insuficiente.");
        }
    }

    @Override
    public void consultarSaldo(String saldo) {
        System.out.println("Saldo atual: R$ " + this.saldo);
        System.out.println("Limite de saque: R$ " + this.limite_saque);
        System.out.println("Limite de transferência: R$ " + this.limite_transf);
    }
}
