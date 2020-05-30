package projeto.idrink.com.br.model;

import com.google.firebase.database.DatabaseReference;

import projeto.idrink.com.br.helper.ConfigFirebase;

public class Produto {

    private String idUsuario;
    private String idProduto;
    private String nomeProduto;
    private String descricao;
    private Double preco;
    private String Categoria;
    private String produtoImg;
    private String quantidade;

    public Produto() {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos");

        setIdProduto(produtoRef.push().getKey());
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        produtoRef.setValue(this);
    }

    public void remover(){

        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produtos").child(getIdUsuario()).child(getIdProduto());
        produtoRef.removeValue();
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getProdutoImg() {
        return produtoImg;
    }

    public void setProdutoImg(String produtoImg) {
        this.produtoImg = produtoImg;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }
}
