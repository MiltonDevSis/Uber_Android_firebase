package br.com.milton.uber_android_firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.helper.Permissoes;
import br.com.milton.uber_android_firebase.helper.UsuarioFirebase;
import br.com.milton.uber_android_firebase.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        // validar permissoes
        Permissoes.validarPermissoes( permissoes, this, 1);

        //autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

    }

    public void abrirTelaLogin(View view){
        startActivity( new Intent(this, LoginActivity.class));
    }

    public void abrirTelaCadastro(View view){
        startActivity( new Intent(this, CadastroActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        UsuarioFirebase.redirecionaUsuarioLogado( MainActivity.this );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResultado : grantResults){
                if ( permissaoResultado == PackageManager.PERMISSION_DENIED ){
                        alertaValidacaoPermissao();
                }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle("Permissões negadas!");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialogInterface, i) -> {
            finish();
        });

        AlertDialog dialog = builder.create();dialog.show();
    }
}