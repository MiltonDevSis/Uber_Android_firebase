package br.com.milton.uber_android_firebase.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText edtCampoNome, edtCampoEmail, edtCampoSenha;
    private Switch switchTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtCampoNome      = findViewById(R.id.edtCadastroNome);
        edtCampoEmail     = findViewById(R.id.edtCadastroEmail);
        edtCampoSenha     = findViewById(R.id.edtCadastroSenha);
        switchTipoUsuario = findViewById(R.id.switchTipoUsuario);
    }

    public void validarCadastroUsuario(View view){

        String textoNome = Objects.requireNonNull(edtCampoNome.getText()).toString();
        String textoEmail = Objects.requireNonNull(edtCampoEmail.getText()).toString();
        String textoSenha = Objects.requireNonNull(edtCampoSenha.getText()).toString();

        if( !textoNome.isEmpty() ) {  //verifica nome
            if( !textoEmail.isEmpty() ) {  //verifica e-mail
                if( !textoSenha.isEmpty() ) {  //verifica senha

                    Usuario usuario = new Usuario();
                    usuario.setNome( textoNome );
                    usuario.setEmail( textoEmail );
                    usuario.setSenha( textoSenha );
                    usuario.setTipo( verificaTipoUsuario() );

                    cadastrarUsuario( usuario );

                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(CadastroActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
        }

    }

    public void cadastrarUsuario( Usuario usuario ){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, task -> {

            if ( task.isSuccessful() ){
                Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar Usuário!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String verificaTipoUsuario(){
        return switchTipoUsuario.isChecked() ? "M" : "P" ;
    }
}