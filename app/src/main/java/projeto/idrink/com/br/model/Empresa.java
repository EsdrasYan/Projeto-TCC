package projeto.idrink.com.br.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import projeto.idrink.com.br.helper.ConfigFirebase;

public class Empresa implements Serializable {

    private String idUsuario;
    private String imgUrl;
    private String nomeEmpresa;
    private String cpfCnpj;
    private String tempoEstimado;
    private Double taxaEntrega;

    public Empresa() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas").child(getIdUsuario());

        empresaRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(String tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public Double getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(Double taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }
}
