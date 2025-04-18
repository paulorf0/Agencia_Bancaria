package classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import enums.Canal;
import enums.TipoTransacao;

public class Transacao {
    // Transação de Cliente para Destino.
    private final int da_conta;
    private final int para_conta;
    private final LocalDateTime data;
    private final TipoTransacao tipo;
    private final BigDecimal valor;
    private final Canal canal;

    public Transacao(int da_conta, LocalDateTime data, TipoTransacao tipo, BigDecimal valor, Canal canal) {
        this.da_conta = da_conta;
        this.para_conta = -1; // Caso a transação foi feito de si para si.
        this.data = data;
        this.tipo = tipo;
        this.valor = valor;
        this.canal = canal;
    }

    public Transacao(int da_conta, int para_conta, LocalDateTime data, TipoTransacao tipo, BigDecimal valor,
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

    public int getDa_conta() {
        return da_conta;
    }

    public int getPara_conta() {
        return para_conta;
    }

}
