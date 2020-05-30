package projeto.idrink.com.br.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import projeto.idrink.com.br.R;
import projeto.idrink.com.br.helper.UsuarioFirebase;
import projeto.idrink.com.br.model.Empresa;
import projeto.idrink.com.br.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editNomeProduto, editDescricaoProduto, editValor, editCategoria, editQtdEstoque;
    private ImageView imgProduto;
    private String idUsuarioLogado;
    private String urlImgProduto = "";
    private static final int SELECAO_GALERIA = 200;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastrar Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    public void validarDadosProduto(View view){

        String nomeProduto = editNomeProduto.getText().toString();
        String descricao = editDescricaoProduto.getText().toString();
        String valor = editValor.getText().toString();
        String categoria = editCategoria.getText().toString();
        String qtd = editQtdEstoque.getText().toString();

        if(!nomeProduto.isEmpty()){
            if(!descricao.isEmpty()){
                if(!valor.isEmpty()){
                    if(!qtd.isEmpty()){
                        if(!categoria.isEmpty()){
                            Produto produto = new Produto();
                            produto.setIdUsuario(idUsuarioLogado);
                            produto.setNomeProduto(nomeProduto);
                            produto.setDescricao(descricao);
                            produto.setCategoria(categoria);
                            produto.setQuantidade(qtd);
                            produto.setPreco(Double.parseDouble(valor));
                            produto.salvar();
                            finish();
                            exibirMensagem("Produto salvo com sucesso!");
                        }else{
                            exibirMensagem("Digite a categoria!");
                        }
                    }else{
                        exibirMensagem("Digite a quantidade em estoque!");
                    }
                }else{
                    exibirMensagem("Digite o valor do produto!");
                }
            }else{
                exibirMensagem("Digite a descrição!");
            }
        }else{
            exibirMensagem("Digite o nome do produto!");
        }
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap img = null;

            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri imgLocal = data.getData();
                        img = MediaStore.Images.Media.getBitmap(getContentResolver(), imgLocal);
                        break;
                }

                if(img != null){
                    imgProduto.setImageBitmap(img);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imgDados = baos.toByteArray();

                    StorageReference imgRef = storageReference.child("imagens").child("empresas").child(idUsuarioLogado + "jpeg");
                    UploadTask uploadTask = imgRef.putBytes(imgDados);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this, "Erro ao tentar fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImgProduto = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(NovoProdutoEmpresaActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes(){
        editNomeProduto = findViewById(R.id.editNomeProduto);
        editDescricaoProduto = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editPreco);
        editCategoria = findViewById(R.id.editCategoria);
        imgProduto = findViewById(R.id.imgProduto);
        editQtdEstoque = findViewById(R.id.editQtdEstoque);
    }
}
