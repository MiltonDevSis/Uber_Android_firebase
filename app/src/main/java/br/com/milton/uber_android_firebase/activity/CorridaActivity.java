package br.com.milton.uber_android_firebase.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.model.Requisicao;
import br.com.milton.uber_android_firebase.model.Usuario;

public class CorridaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMotorista;
    private Usuario motorista;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;
    private Button btnAceitarCorrida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        inicializarComponentes();

        // recupera dados do usuario
        if (getIntent().getExtras().containsKey("idRequisicao") && getIntent().getExtras().containsKey("motorista")) {
            Bundle extra = getIntent().getExtras();
            motorista = (Usuario) extra.getSerializable("motorista");
            idRequisicao = extra.getString("idRequisicao");
            verificaStatusRequisicao();
        }

    }

    public void verificaStatusRequisicao(){

        DatabaseReference requisicoes = firebaseRef.child("requisicoes").child(idRequisicao);
        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requisicao = snapshot.getValue(Requisicao.class);

                switch (requisicao.getStatus()){
                    case Requisicao.STATUS_AGUARDANDO:
                            requisicaoAguardando();
                        break;
                    case Requisicao.STATUS_A_CAMINHO:
                        requisicaoACaminho();
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void requisicaoAguardando(){
        btnAceitarCorrida.setText("Aceitar Corrida");
    }

    private void requisicaoACaminho(){

        btnAceitarCorrida.setText("A caminho do passageiro");

    }

    public void aceitarCorrida(View view) {

        // configurar requisicao
        requisicao = new Requisicao();
        requisicao.setId(idRequisicao);
        requisicao.setMotorista(motorista);
        requisicao.setStatus(Requisicao.STATUS_A_CAMINHO);

        requisicao.atualizar();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        recuperarLocalizacaoUsuario();
    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                localMotorista = new LatLng(latitude, longitude);

                mMap.clear(); // limpa o mapa e mostra um unico marcador
                mMap.addMarker(new MarkerOptions().position(localMotorista).title("Meu Local").icon(BitmapDescriptorFactory.fromResource(R.drawable.carro)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localMotorista, 15));

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

    public void inicializarComponentes() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAceitarCorrida = findViewById(R.id.btnAceitarCorrida);

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        getSupportActionBar().setTitle("Iniciar Corrida");
        //getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
}