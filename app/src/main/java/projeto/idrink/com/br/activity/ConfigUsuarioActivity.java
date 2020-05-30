package projeto.idrink.com.br.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import projeto.idrink.com.br.R;
import projeto.idrink.com.br.helper.ConfigFirebase;
import projeto.idrink.com.br.helper.UsuarioFirebase;
import projeto.idrink.com.br.model.Usuario;

public class ConfigUsuarioActivity extends AppCompatActivity {

    private EditText editNomeUsuario, editCep, editEndereco, editComplemento, editCidade, editBairro;
    private String idUsuario;
    private DatabaseReference firebaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_usuario);

        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfigFirebase.getFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario(){

        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editNomeUsuario.setText(usuario.getNomeUsuario());
                    editCep.setText(usuario.getCep());
                    editEndereco.setText(usuario.getEndereco());
                    editComplemento.setText(usuario.getComplemento());
                    editCidade.setText(usuario.getCidade());
                    editBairro.setText(usuario.getBairro());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void validarDadosUsuario(View view){
        String nomeUsuario = editNomeUsuario.getText().toString();
        String cep = editCep.getText().toString();
        String endereco = editEndereco.getText().toString();
        String complemento = editComplemento.getText().toString();
        String cidade = editCidade.getText().toString();
        String bairro = editBairro.getText().toString();

        if(!nomeUsuario.isEmpty()){
            if(!cep.isEmpty()){
                if(!endereco.isEmpty()){
                    if(!complemento.isEmpty()){
                        if(!cidade.isEmpty()){
                            if(!bairro.isEmpty()){

                                Usuario usuario = new Usuario();

                                usuario.setIdUsuario(idUsuario);
                                usuario.setNomeUsuario(nomeUsuario);
                                usuario.setCep(cep);
                                usuario.setEndereco(endereco);
                                usuario.setComplemento(complemento);
                                usuario.setCidade(cidade);
                                usuario.setBairro(bairro);
                                usuario.salvar();

                                exibirMensagem("Dados atualizados com sucesso!");
                                finish();

                            }else{
                                exibirMensagem("Digite um Bairro!");
                            }
                        }else{
                            exibirMensagem("Digite uma Cidade!");
                        }
                    }else{
                        exibirMensagem("Digite um Complemento!");
                    }
                }else{
                    exibirMensagem("Digite um Endereço!");
                }
            }else{
                exibirMensagem("Digite um CEP!");
            }
        }else{
            exibirMensagem("Digite um nome!");
        }

    }

    private void inicializarComponentes(){
        editNomeUsuario = findViewById(R.id.editNomeUsuario);
        editCep = findViewById(R.id.editCep);
        editEndereco = findViewById(R.id.editEndereco);
        editComplemento = findViewById(R.id.editComplemento);
        editCidade = findViewById(R.id.editCidade);
        editBairro = findViewById(R.id.editBairro);

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

}
