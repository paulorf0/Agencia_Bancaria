package classes;

public class Agencia {
    private int nro;
    private String nome;
    private Endereco endereco;

    public Agencia(int nro, String nome, Endereco endereco) {
        this.nro = nro;
        this.nome = nome;
        this.endereco = endereco;
    }

    public int getNro() {
        return nro;
    }

    public String getNome() {
        return nome;
    }

    public Endereco getEndereco() {
        return endereco;
    }

}