package classes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import enums.Sexo;

public class Gerente extends Funcionario {
    private LocalDate data_ingr_gerente;
    private List<String> cursos;
    private static BigDecimal comissao;

    public Gerente(int nro_cart, int nro_agencia, Sexo sexo, BigDecimal salario, LocalDate anoIngresso,
            LocalDate data_ingr_gerente, List<String> cursos, BigDecimal comissao) {
        super(nro_cart, nro_agencia, sexo, salario, anoIngresso);
        this.data_ingr_gerente = data_ingr_gerente;
        this.cursos = cursos;
        this.comissao = comissao;
        setCargo("Gerente");
    }

    public BigDecimal CalcSal() {
        return super.getSalario().add(comissao);
    }

    public String getCursosString() {
        StringBuilder cString = new StringBuilder();

        for (String c : cursos) {
            cString.append(c).append(", ");
        }

        if (cString.length() > 0) {
            cString.setLength(cString.length() - 2);
        }

        return cString.toString();
    }

    public static BigDecimal getComissao() {
        return comissao;
    }

    public static void setComissao(BigDecimal comis) {
        comissao = comis;
    }

    public LocalDate getData_ingr_gerente() {
        return data_ingr_gerente;
    }

    public List<String> getCursos() {
        return cursos;
    }

    public void setData_ingr_gerente(LocalDate data_ingr_gerente) {
        this.data_ingr_gerente = data_ingr_gerente;
    }

    public void setCursos(List<String> cursos) {
        this.cursos = cursos;
    }

}
