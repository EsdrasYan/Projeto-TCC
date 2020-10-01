package projeto.idrink.com.br.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import projeto.idrink.com.br.R;
import projeto.idrink.com.br.helper.ConfigFirebase;
import projeto.idrink.com.br.helper.UsuarioFirebase;
import projeto.idrink.com.br.model.Empresa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button btnAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);


        iniciarComponentes();
        autenticacao = ConfigFirebase.getFirebaseAutenticacao();
        autenticacao.signOut();

        //Verificar Usuário Logado
        verificarLogin();
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ //empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else{ //usuário
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });
        btnAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if(!email.isEmpty()){
                    if(!senha.isEmpty()){
                        //Verificar o Switch
                        if(tipoAcesso.isChecked()){
                            //Cadastro
                            autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(AutenticacaoActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();
                                       String tipoUsuario = getTipoUsuario();
                                       UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                       Empresa empresa = new Empresa();
                                       Integer img = R.drawable.perfil;
                                       empresa.setImgUrl(img.toString());

                                        abrirTelaHome(tipoUsuario);
                                    }else{
                                        String erroExcecao = "";

                                        try{
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            erroExcecao = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erroExcecao = "Digite um e-mail válido!";
                                        }catch(FirebaseAuthUserCollisionException e){
                                            erroExcecao = "E-mail já utilizado!";
                                        }catch (Exception e){
                                            erroExcecao = "Erro: " + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(AutenticacaoActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            //Login
                            autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AutenticacaoActivity.this, "logado com sucesso!", Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = task.getResult().getUser().getDisplayName();
                                        abrirTelaHome(tipoUsuario);
                                    }else{
                                        Toast.makeText(AutenticacaoActivity.this, "Erro ao fazer login: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else{
                        Toast.makeText(AutenticacaoActivity.this, "Preencha o campo de Senha!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(AutenticacaoActivity.this, "Preencha o campo de E-mail!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void verificarLogin(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaHome(tipoUsuario);
        }
    }

    private String getTipoUsuario(){
       return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void abrirTelaHome(String tipoUsuario){
       if(tipoUsuario.equals("E")){ //empresa
           startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
       }else{ //Usuário
           startActivity(new Intent(getApplicationContext(), HomeActivity.class));
       }
    }
    private void iniciarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        btnAcessar = findViewById(R.id.btnAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }
}
