package br.com.milton.uber_android_firebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.helper.UsuarioFirebase;
import br.com.milton.uber_android_firebase.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText edtCampoNome, edtCampoEmail, edtCampoSenha;
    private Switch switchTipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtCampoNome = findViewById(R.id.edtCadastroNome);
        edtCampoEmail = findViewById(R.id.edtCadastroEmail);
        edtCampoSenha = findViewById(R.id.edtCadastroSenha);
        switchTipoUsuario = findViewById(R.id.switchTipoUsuario);
    }

    public void validarCadastroUsuario(View view) {

        String textoNome = Objects.requireNonNull(edtCampoNome.getText()).toString();
        String textoEmail = Objects.requireNonNull(edtCampoEmail.getText()).toString();
        String textoSenha = Objects.requireNonNull(edtCampoSenha.getText()).toString();

        if (!textoNome.isEmpty()) {  //verifica nome
            if (!textoEmail.isEmpty()) {  //verifica e-mail
                if (!textoSenha.isEmpty()) {  //verifica senha

                    Usuario usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);
                    usuario.setTipo(verificaTipoUsuario());

                    cadastrarUsuario(usuario);

                } else {
                    Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CadastroActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarUsuario(Usuario usuario) {

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()) {

                try {

                    String idUsuario = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    usuario.setId(idUsuario);
                    usuario.salvar();

                    // Atualizar nome no UserProfile
                    UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );

                    // redireciona o usuario com base no seu tipo
                    // se o usuario for passageiro chama a mapsActivity
                    // senão chama a activity requisiçoes

                    if (verificaTipoUsuario().equals("P")) {
                        startActivity(new Intent(CadastroActivity.this, PassageiroActivity.class));
                        finish();

                        Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar Passageiro", Toast.LENGTH_LONG).show();
                    } else {
                        startActivity(new Intent(CadastroActivity.this, RequisicoesActivity.class));
                        finish();

                        Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar Motorista", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

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
                Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_LONG).show();
            }
        });

    }

    public String verificaTipoUsuario() {
        return switchTipoUsuario.isChecked() ? "M" : "P";
    }
}