package classes;

import java.time.LocalDate;

import enums.EstadoCivil;

public abstract class Pessoa {

    protected String nomeCompleto;
    protected String cpf;
    protected String rg;
    protected String senha;
    protected LocalDate dataNascimento;
    protected Endereco endereco;
    protected EstadoCivil estadoCivil;
}
