package projeto.idrink.com.br.model;

import com.google.firebase.database.DatabaseReference;

import projeto.idrink.com.br.helper.ConfigFirebase;

public class Usuario {

    private String idUsuario;
    private String nomeUsuario;
    private String cep;
    private String endereco;
    private String complemento;
    private String cidade;
    private String bairro;

    public Usuario() {

    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(getIdUsuario());
        usuarioRef.setValue(this);

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
}
