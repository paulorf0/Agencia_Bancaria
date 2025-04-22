package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import enums.Canal;
import enums.TipoTransacao;

public class Transacao {
    // Transação de Cliente para Destino.
    private final UUID da_conta;
    private final UUID para_conta;
    private final LocalDateTime data;
    private final TipoTransacao tipo;
    private final BigDecimal valor;
    private final Canal canal;

    public Transacao(UUID da_conta, LocalDateTime data, TipoTransacao tipo, BigDecimal valor, Canal canal) {
        this.da_conta = da_conta;
        this.para_conta = null; // Caso a transação foi feito de si para si.
        this.data = data;
        this.tipo = tipo;
        this.valor = valor;
        this.canal = canal;
    }

    public Transacao(UUID da_conta, UUID para_conta, LocalDateTime data, TipoTransacao tipo, BigDecimal valor,
            Canal canal) {
        this.da_conta = da_conta;
        this.para_conta = para_conta;
        this.data = data;
        this.tipo = tipo;
        this.valor = valor;
        this.canal = canal;
    }

    public LocalDateTime getData() {
        return data;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Canal getCanal() {
        return canal;
    }

    public UUID getDa_conta() {
        return da_conta;

    }

    public UUID getPara_conta() {
        return para_conta;
    }

}