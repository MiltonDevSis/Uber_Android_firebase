package br.com.milton.uber_android_firebase.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.adapter.RequisicoesAdapter;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.helper.UsuarioFirebase;
import br.com.milton.uber_android_firebase.model.Requisicao;
import br.com.milton.uber_android_firebase.model.Usuario;

public class RequisicoesActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private Usuario motorista;

    private RecyclerView recyclerRequisicoes;
    private TextView txtResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        inicializarComponentes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuSair :
                autenticacao.signOut();
                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        getSupportActionBar().setTitle("Requisições");

        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        txtResultado = findViewById(R.id.txtResultado);

        // Configurações inicaiais
        motorista = UsuarioFirebase.getDadosUsuarioLogado();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // configurar recyclerView
        adapter = new RequisicoesAdapter( listaRequisicoes, getApplicationContext(), motorista );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerRequisicoes.setLayoutManager( layoutManager );
        recyclerRequisicoes.setHasFixedSize( true );
        recyclerRequisicoes.setAdapter( adapter );

        recuperarRequisicoes();
    }

    private void recuperarRequisicoes(){

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicaoPesquisa = requisicoes.orderByChild("status").equalTo(Requisicao.STATUS_AGUARDANDO);

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() > 0){

                    txtResultado.setVisibility(View.GONE);
                    recyclerRequisicoes.setVisibility(View.VISIBLE);

                }else{

                    txtResultado.setVisibility(View.VISIBLE);
                    recyclerRequisicoes.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: snapshot.getChildren()){
                    Requisicao requisicao = ds.getValue( Requisicao.class );

                    listaRequisicoes.add( requisicao );
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}