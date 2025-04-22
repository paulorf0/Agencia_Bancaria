package classes;

import java.math.BigDecimal;
import java.time.LocalDate;

import enums.Sexo;

public class Funcionario extends Pessoa {
    private int nro_cart;
    private String cargo;
    private Sexo sexo;
    private BigDecimal salario;
    private LocalDate anoIngresso;

    public BigDecimal CalcSalario() {
        if (anoIngresso.isBefore(LocalDate.now().minusYears(15)))
            return salario.multiply(new BigDecimal("1.1"));
        else
            return salario;
    }

    public BigDecimal getSalario() {
        return salario;
    }
}
