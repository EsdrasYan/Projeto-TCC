package projeto.idrink.com.br.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import projeto.idrink.com.br.helper.ConfigFirebase;

public class Pedido {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String cep;
    private String endereco;
    private String complemento;
    private String cidade;
    private String bairro;
    private List<ItemPedido> items;
    private Double valorTotal;
    private String status = "pendente";
    private int tipoPagamento;
    private String obs;

    public Pedido() {
    }

    public Pedido(String idUsuarioAux, String idEmpresaAux) {

        setIdUsuario(idUsuarioAux);
        setIdEmpresa(idEmpresaAux);

        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("usuario_pedidos").child(idEmpresaAux).child(idUsuarioAux);
        setIdPedido(pedidoRef.push().getKey());
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("usuario_pedidos").child(getIdEmpresa()).child(getIdUsuario());

        pedidoRef.setValue(this);

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public List<ItemPedido> getItems() {
        return items;
    }

    public void setItems(List<ItemPedido> items) {
        this.items = items;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(int tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
