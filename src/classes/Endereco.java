package classes;

public class Endereco {
    private String cidade;
    private String estado;
    private String bairro;
    private int nro_local;

    public Endereco(String cidade, String estado, String bairro, int nro_local) {
        this.cidade = cidade;
        this.estado = estado;
        this.bairro = bairro;
        this.nro_local = nro_local;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getBairro() {
        return bairro;
    }

    public int getNro_local() {
        return nro_local;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setNro_local(int nro_local) {
        this.nro_local = nro_local;
    }

}
