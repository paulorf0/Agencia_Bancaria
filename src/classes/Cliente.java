package classes;

public class Cliente {
    private String cpf;
    private String nome;
    private Conta conta;

    public Cliente(String cpf, String nome, Conta conta) {
        this.cpf = cpf;
        this.nome = nome;
        this.conta = conta;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }
}
