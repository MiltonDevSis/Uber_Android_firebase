package br.com.milton.uber_android_firebase.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.helper.UsuarioFirebase;
import br.com.milton.uber_android_firebase.model.Destino;
import br.com.milton.uber_android_firebase.model.Requisicao;
import br.com.milton.uber_android_firebase.model.Usuario;

public class PassageiroActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText edtDestino;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button btnChamarUber;
    private LinearLayout linearLayoutDestino;
    private boolean uberChamado = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;

    private LatLng localPassageiro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiro);

        inicializarComponentes();

        verificaStatusRequisicao();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        recuperarLocalizacaoUsuario();
    }

    private void verificaStatusRequisicao(){

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("passageiro/id").equalTo( usuarioLogado.getId() );

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Requisicao> lista = new ArrayList<>();

               for (DataSnapshot ds: snapshot.getChildren()){
                    lista.add( ds.getValue( Requisicao.class ) );

               }

                Collections.reverse( lista );

               if (lista != null && lista.size() > 0) {

                   requisicao = lista.get(0);

                   switch (requisicao.getStatus()) {
                       case Requisicao.STATUS_AGUARDANDO:
                           linearLayoutDestino.setVisibility(View.GONE);
                           btnChamarUber.setText("Cancelar Uber");
                           uberChamado = true;
                           break;
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void chamarUber( View view ){

        if ( !uberChamado ){  // uber não foi chamado

            String enderecoDestino = edtDestino.getText().toString();

            if ( !enderecoDestino.equals("") || enderecoDestino != null ){

                Address addressDestino = recuperarEndereco( enderecoDestino );
                if ( addressDestino != null ){

                    Destino destino = new Destino();
                    destino.setCidade(addressDestino.getSubAdminArea());
                    destino.setCep( addressDestino.getPostalCode() );
                    destino.setBairro( addressDestino.getSubLocality() );
                    destino.setRua( addressDestino.getThoroughfare() );
                    destino.setNumero( addressDestino.getFeatureName() );
                    destino.setLatitude( String.valueOf(addressDestino.getLatitude()) );
                    destino.setLongitude( String.valueOf(addressDestino.getLongitude()) );

                    StringBuilder mensagem = new StringBuilder();
                    mensagem.append("Cidade: " + destino.getCidade());
                    mensagem.append("\nRua: " + destino.getRua());
                    mensagem.append("\nBairro: " + destino.getBairro());
                    mensagem.append("\nNúmero: " + destino.getNumero());
                    mensagem.append("\nCep: " + destino.getCep());

                    AlertDialog.Builder builder = new AlertDialog.Builder( this );
                    builder.setTitle("Confirme seu endereço");
                    builder.setMessage( mensagem );
                    builder.setPositiveButton("Confirmar", (dialogInterface, i) -> {

                        // salvar requisição
                        salvarRequisicao( destino );
                        uberChamado = true;

                    }).setNegativeButton("Cancelar", (dialogInterface, i) -> {

                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }else{
                Toast.makeText(this, "Informe o endereço de destino", Toast.LENGTH_LONG).show();
            }
        }else{  // cancelar a requisição

            uberChamado = false;
        }
    }

    private void salvarRequisicao(Destino destino){

        Requisicao requisicao = new Requisicao();
        requisicao.setDestino( destino );

        Usuario usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioPassageiro.setLatitude( String.valueOf(localPassageiro.latitude) );
        usuarioPassageiro.setLongitude(String.valueOf(localPassageiro.longitude));
        requisicao.setPassageiro( usuarioPassageiro );

        requisicao.setStatus( Requisicao.STATUS_AGUARDANDO );
        requisicao.salvar();

        linearLayoutDestino.setVisibility( View.GONE );
        btnChamarUber.setText("Cancelar Uber");

    }

    private Address recuperarEndereco(String endereco){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if( listaEnderecos != null && listaEnderecos.size() > 0 ){
                Address address = listaEnderecos.get(0);

                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                localPassageiro = new LatLng(latitude, longitude);

                mMap.clear(); // limpa o mapa e mostra um unico marcador
                mMap.addMarker(new MarkerOptions().position(localPassageiro).title("Meu Local").icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localPassageiro, 15));

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }
        };

        // solicita atualizacao de localizacao
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Inciar uma viagem");
        setSupportActionBar(toolbar);

        edtDestino = findViewById(R.id.edtDestino);
        linearLayoutDestino = findViewById(R.id.LinearLayoutDestino);
        btnChamarUber = findViewById(R.id.btnChamarUber);

        // Configurações inicaiais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}