package br.com.milton.uber_android_firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.helper.UsuarioFirebase;
import br.com.milton.uber_android_firebase.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.edtEmailLogin);
        campoSenha = findViewById(R.id.edtSenhaLogin);

    }

    public void validarLoginUsuario(View view) {

        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if (!textoEmail.isEmpty()) {
            if (!textoSenha.isEmpty()) {

                Usuario usuario = new Usuario();
                usuario.setEmail( textoEmail );
                usuario.setSenha( textoSenha );

                logarUsuario( usuario );

            } else {
                Toast.makeText(LoginActivity.this, "Preencha o senha!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Preencha a email!", Toast.LENGTH_LONG).show();
        }
    }

    public void logarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                //  Verificar o tipo de usuario logado "Motorista" / "passageiro"
                UsuarioFirebase.redirecionaUsuarioLogado( LoginActivity.this );

            }else {

                String excecao;
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthWeakPasswordException e) {
                    excecao = "Digite uma senha mais forte";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    excecao = "Digite um Email válido";
                } catch (FirebaseAuthUserCollisionException e) {
                    excecao = "Conta já cadastrada";
                } catch (Exception e) {
                    excecao = "Erro ao cadastrar usuário " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_LONG).show();

            }
        });

    }
}