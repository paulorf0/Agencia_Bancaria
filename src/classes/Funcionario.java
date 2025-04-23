package classes;

import java.math.BigDecimal;
import java.time.LocalDate;

import enums.Sexo;

public class Funcionario extends Pessoa {
    private int nro_cart;
    private String cargo;
    private int nro_agencia;
    private Sexo sexo;
    private BigDecimal salario;
    private LocalDate anoIngresso;

    public Funcionario(int nro_cart, String cargo, int nro_agencia, Sexo sexo, BigDecimal salario,
            LocalDate anoIngresso) {
        this.nro_cart = nro_cart;
        this.cargo = cargo;
        this.nro_agencia = nro_agencia;
        this.sexo = sexo;
        this.salario = salario;
        this.anoIngresso = anoIngresso;
    }

    public Funcionario(int nro_cart, int nro_agencia, Sexo sexo, BigDecimal salario,
            LocalDate anoIngresso) {
        this.nro_cart = nro_cart;
        this.nro_agencia = nro_agencia;
        this.sexo = sexo;
        this.salario = salario;
        this.anoIngresso = anoIngresso;
    }

    public BigDecimal CalcSalario() {
        if (anoIngresso.isBefore(LocalDate.now().minusYears(15)))
            return salario.multiply(new BigDecimal("1.1"));
        else
            return salario;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public int getNro_cart() {
        return nro_cart;
    }

    public void setNro_cart(int nro_cart) {
        this.nro_cart = nro_cart;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public LocalDate getAnoIngresso() {
        return anoIngresso;
    }

    public void setAnoIngresso(LocalDate anoIngresso) {
        this.anoIngresso = anoIngresso;
    }

    public int getNro_agencia() {
        return nro_agencia;
    }

}
