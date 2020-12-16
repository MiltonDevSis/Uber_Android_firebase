package br.com.milton.uber_android_firebase.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

import br.com.milton.uber_android_firebase.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
;
    }

    public void abrirTelaLogin(View view){

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity( intent );
    }

    public void abrirTelaCadastro(View view){

        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity( intent );
    }
}