package br.com.milton.uber_android_firebase.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.helper.UsuarioFirebase;
import br.com.milton.uber_android_firebase.model.Usuario;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

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
}