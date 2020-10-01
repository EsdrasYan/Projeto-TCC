package projeto.idrink.com.br.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import projeto.idrink.com.br.R;
import projeto.idrink.com.br.adapter.AdapterProduto;
import projeto.idrink.com.br.helper.ConfigFirebase;
import projeto.idrink.com.br.helper.UsuarioFirebase;
import projeto.idrink.com.br.listener.RecyclerItemClickListener;
import projeto.idrink.com.br.model.Empresa;
import projeto.idrink.com.br.model.ItemPedido;
import projeto.idrink.com.br.model.Pedido;
import projeto.idrink.com.br.model.Produto;
import projeto.idrink.com.br.model.Usuario;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imgEmpresaCardapio;
    private TextView txtNomeEmpresaCardapio;
    private Empresa empresaEscolhida;
    private TextView textCarrinhoQtd, txtVlTotal;
    private AlertDialog dialog;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itemsCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String idEmpresa;
    private Usuario usuario;
    private Pedido pedidoRecuperado;

    private int tipoPagamento;

    private int qtdItems;
    private Double vlTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        inicializarComponentes();
        firebaseRef = ConfigFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Recuperando empresa escolhida
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            empresaEscolhida = (Empresa) bundle.getSerializable("empresa");

            txtNomeEmpresaCardapio.setText(empresaEscolhida.getNomeEmpresa());
            idEmpresa = empresaEscolhida.getIdUsuario(); //ID da Empresa

            String url = empresaEscolhida.getImgUrl();
            Picasso.get().load(url).into(imgEmpresaCardapio);
        }

        Toolbar toolbar = findViewById(R.id.toolbarProdutos);
        toolbar.setTitle("Cardapio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //RecyclerView
        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        recyclerProdutosCardapio.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerProdutosCardapio,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        confirmarQtd(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        recuperarProdutos();
        recuperarDadosUsuario();
    }

    private void confirmarQtd(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Digite a quantidade");

        final EditText editQtd = new EditText(this);
        editQtd.setText("1");

        builder.setView(editQtd);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String qtd = editQtd.getText().toString();
                int qtdEstoque;
                int qtdAux = Integer.parseInt(qtd);
                int qtdFinal = 0;

                Produto produtoSelecionado = produtos.get(position);
                ItemPedido itemPedido = new ItemPedido();

                qtdEstoque = Integer.parseInt(produtoSelecionado.getQuantidade());

                if(qtdAux > qtdEstoque){
                    Toast.makeText(CardapioActivity.this, "Quantidade indisponível!", Toast.LENGTH_SHORT).show();
                }else{
                    itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                    itemPedido.setNomeProduto(produtoSelecionado.getNomeProduto());
                    itemPedido.setPreco(produtoSelecionado.getPreco());
                    itemPedido.setQuantidade(Integer.parseInt(qtd));
                    itemsCarrinho.add(itemPedido);

                    qtdFinal = qtdEstoque - qtdAux;

                    produtoSelecionado.setQuantidade(String.valueOf(qtdFinal));

                    if(pedidoRecuperado == null){
                        pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                    }

                    pedidoRecuperado.setNome(usuario.getNomeUsuario());
                    pedidoRecuperado.setCep(usuario.getCep());
                    pedidoRecuperado.setEndereco(usuario.getEndereco());
                    pedidoRecuperado.setComplemento(usuario.getComplemento());
                    pedidoRecuperado.setCidade(usuario.getCidade());
                    pedidoRecuperado.setBairro(usuario.getBairro());

                    pedidoRecuperado.setItems(itemsCarrinho);
                    pedidoRecuperado.salvar();
                    produtoSelecionado.salvar();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void recuperarDadosUsuario(){

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando...").setCancelable(false).build();
        dialog.show();

        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(idUsuarioLogado);

        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPedido(){

        DatabaseReference pedidoRef = firebaseRef.child("usuarioPedidos").child(idEmpresa).child(idUsuarioLogado);

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                qtdItems = 0;
                vlTotal = 0.0;
                itemsCarrinho = new ArrayList<>();

                if(dataSnapshot.getValue() != null){

                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itemsCarrinho = pedidoRecuperado.getItems();

                    for(ItemPedido itemPedido: itemsCarrinho){
                        int quantidade = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        vlTotal += (quantidade * preco);
                        qtdItems += quantidade;
                    }
                }

                DecimalFormat formato = new DecimalFormat("0.00");

                textCarrinhoQtd.setText("Quantidade: " + String.valueOf(qtdItems));
                txtVlTotal.setText("R$" + formato.format(vlTotal));
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarProdutos(){
        final DatabaseReference produtosRef = firebaseRef.child("produtos").child(idEmpresa);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_cardapio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuPedido:
                confirmarPedido();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

        public void abrirTelaCarrinho(View view){
            Intent intent = new Intent(CardapioActivity.this, TelaCarrinhoActivity.class);
            startActivity(intent);
        }

        private void confirmarPedido(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forma de Pagamento");

        CharSequence[] items = new CharSequence[]{
                "Dinheiro", "Cartão"
        };
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tipoPagamento = which;
            }
        });

        final EditText editObs = new EditText(this);
        editObs.setHint("Observação");
        builder.setView(editObs);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String observacao = editObs.getText().toString();

                if(tipoPagamento == 0){
                    pedidoRecuperado.setTipoPagamento(tipoPagamento);
                    pedidoRecuperado.setObs(observacao);
                    pedidoRecuperado.setStatus("confirmado");
                    pedidoRecuperado.confirmar();
                    pedidoRecuperado.remover();
                    pedidoRecuperado = null;
                }else if(tipoPagamento == 1){
                    pedidoRecuperado.setTipoPagamento(tipoPagamento);
                    pedidoRecuperado.setObs(observacao);
                    pedidoRecuperado.setStatus("confirmado");
                    pedidoRecuperado.confirmar();
                    pedidoRecuperado.remover();
                    pedidoRecuperado = null;

                    Intent i = new Intent(CardapioActivity.this, CartaoPagamento.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        }

    private void inicializarComponentes(){

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutoCardapio);
        imgEmpresaCardapio = findViewById(R.id.imgEmpresaCardapio);
        txtNomeEmpresaCardapio = findViewById(R.id.txtNomeEmpresaCardapio);
        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        txtVlTotal = findViewById(R.id.txtVlTotal);

    }
}
