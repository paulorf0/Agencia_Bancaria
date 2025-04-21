package classes;

// 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ContaCorrente extends Conta {
    private BigDecimal limite_cheque_especial;
    private BigDecimal taxa_administrativa;

    public ContaCorrente(String senha, BigDecimal saldo, LocalDate data_abertura,
            BigDecimal limite_cheque_especial, BigDecimal taxa_administrativa, UUID nro_agencia) {
        super(senha, saldo, data_abertura.atStartOfDay(), nro_agencia);
        this.limite_cheque_especial = limite_cheque_especial;
        this.taxa_administrativa = taxa_administrativa;
        this.tipoConta = 0;
    }

    public ContaCorrente(String senha, BigDecimal saldo, LocalDateTime dataAbertura, UUID nro_agencia) {
        super(senha, saldo, dataAbertura, nro_agencia);
        this.limite_cheque_especial = new BigDecimal("500.00"); // valor padrão
        this.taxa_administrativa = new BigDecimal("10.00"); // valor padrão
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
                this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para saque ou saldo insuficiente.");
        }
    }

    @Override
    public void transferir(Conta conta_destino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 &&
                this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
            this.saldo = this.saldo.subtract(valor);
            conta_destino.depositar(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para transferência ou saldo insuficiente.");
        }
    }

    @Override
    public void efetuarPagamento(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) > 0 &&
                this.saldo.add(limite_cheque_especial).compareTo(valor) >= 0) {
            this.saldo = this.saldo.subtract(valor);
            this.ult_movimentacao = LocalDateTime.now();
        } else {
            System.out.println("Valor inválido para pagamento ou saldo insuficiente.");
        }
    }

    @Override
    public void consultarSaldo(String saldo) {
        System.out.println("Saldo atual: R$ " + this.saldo);
        System.out.println("Limite cheque especial: R$ " + this.limite_cheque_especial);
        System.out.println("Taxa administrativa: R$ " + this.taxa_administrativa);
    }
}
