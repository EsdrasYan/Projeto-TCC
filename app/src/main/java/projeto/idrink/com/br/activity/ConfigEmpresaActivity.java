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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import projeto.idrink.com.br.R;
import projeto.idrink.com.br.helper.ConfigFirebase;
import projeto.idrink.com.br.helper.UsuarioFirebase;
import projeto.idrink.com.br.model.Empresa;

public class ConfigEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editCpfCnpj, editTempoEstimado, editTaxaEntrega;
    private ImageView imgPerfilEmpresa;

    private static final int SELECAO_GALERIA = 200;
    private DatabaseReference firebaseRef;
    private StorageReference storageReference;
    private String idUsuarioLogado;
    private String urlImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);

        inicializarComponentes();
        storageReference = ConfigFirebase.getFirebaseStorage();
        firebaseRef = ConfigFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgPerfilEmpresa.setOnClickListener(new View.OnClickListener() {
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

        recuperarDadosEmpresa();
    }

    private void recuperarDadosEmpresa(){

        DatabaseReference empresaRef = firebaseRef.child("empresas").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNomeEmpresa());
                    editCpfCnpj.setText(empresa.getCpfCnpj());
                    editTaxaEntrega.setText(empresa.getTaxaEntrega().toString());
                    empresa.setTempoEstimado(empresa.getTempoEstimado());

                    urlImg = empresa.getImgUrl();
                    if(urlImg != ""){
                        Picasso.get().load(urlImg).into(imgPerfilEmpresa);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validarDadosEmpresa(View view){

        String nomeEmpresa = editEmpresaNome.getText().toString();
        String cpfCnpj = editCpfCnpj.getText().toString();
        String tempoEstimado = editTempoEstimado.getText().toString();
        String taxaEntrega = editTaxaEntrega.getText().toString();

        if(!nomeEmpresa.isEmpty()){
            if(!cpfCnpj.isEmpty()){
                if(!tempoEstimado.isEmpty()){
                    if(!taxaEntrega.isEmpty()){
                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNomeEmpresa(nomeEmpresa);
                        empresa.setTaxaEntrega(Double.parseDouble(taxaEntrega));
                        empresa.setCpfCnpj(cpfCnpj);
                        empresa.setTempoEstimado(tempoEstimado);
                        empresa.setImgUrl(urlImg);
                        empresa.salvar();
                        finish();
                    }else{
                        exibirMensagem("Digite a taxa de entrega!");
                    }
                }else{
                    exibirMensagem("Digite o tempo estimado para a empresa!");
                }
            }else{
                exibirMensagem("Digite o CPF/CNPJ!");
            }
        }else{
            exibirMensagem("Digite um nome para a empresa!");
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
                    imgPerfilEmpresa.setImageBitmap(img);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imgDados = baos.toByteArray();

                    StorageReference imgRef = storageReference.child("imagens").child("empresas").child(idUsuarioLogado + "jpeg");
                    UploadTask uploadTask = imgRef.putBytes(imgDados);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigEmpresaActivity.this, "Erro ao tentar fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImg = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfigEmpresaActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes(){
        editEmpresaNome = findViewById(R.id.editNomeEmpresa);
        editCpfCnpj = findViewById(R.id.editCpfCnpj);
        editTempoEstimado = findViewById(R.id.editTempoEstimado);
        editTaxaEntrega = findViewById(R.id.editTaxaEntrega);
        imgPerfilEmpresa = findViewById(R.id.imgPerfilEmpresa);
    }
}
