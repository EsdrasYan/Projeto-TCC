package projeto.idrink.com.br.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import projeto.idrink.com.br.R;
import projeto.idrink.com.br.model.Produto;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNomeProduto());
        holder.descricao.setText(produto.getDescricao());
        holder.qtd.setText("Quantidade em Estoque: " + produto.getQuantidade());
        holder.valor.setText("R$ " + produto.getPreco());

        String urlImagem = produto.getProdutoImg();
        Picasso.get().load(urlImagem).into(holder.imgProduto);
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduto;
        TextView nome;
        TextView descricao;
        TextView qtd;
        TextView valor;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeProduto);
            descricao = itemView.findViewById(R.id.textDescricao);
            qtd = itemView.findViewById(R.id.textQtdEstoque);
            valor = itemView.findViewById(R.id.textPreco);
            imgProduto = itemView.findViewById(R.id.imageProduto);
        }
    }
}
