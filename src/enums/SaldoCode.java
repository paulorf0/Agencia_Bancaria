package enums;

public enum SaldoCode {
    SAQUE_NEGATIVO("Valor de saque inválido", "000"),
    TRANSFERENCIA_NEGATIVA("Valor de transferência inválida", "001"),
    DEPOSITO_NEGATIVO("Valor de deposito inválido", "002"),
    SALDO_INSUFICIENTE("Saldo na conta insuficiente", "003"),
    LIMITE_TRANSF("Sua conta não permite esse valor para transferência", "004"),
    VALOR_INVALIDO("Valor invalido.", "005"),
    ;

    private String code;
    private String msg;

    SaldoCode(String msg, String code) {
        this.msg = msg;
        this.code = code;

    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

}
