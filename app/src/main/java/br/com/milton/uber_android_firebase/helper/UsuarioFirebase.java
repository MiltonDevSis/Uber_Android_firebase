package br.com.milton.uber_android_firebase.helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.milton.uber_android_firebase.activity.MapsActivity;
import br.com.milton.uber_android_firebase.activity.RequisicoesActivity;
import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;
import br.com.milton.uber_android_firebase.model.Usuario;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarNomeUsuario(String nome) {

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();

            user.updateProfile(profile).addOnCompleteListener(task -> {

                if(!task.isSuccessful()){
                    Log.d("Perfil", "Erro ao atualizar no de perfil");
                }
            });
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void redirecionaUsuarioLogado(Activity activity){

        FirebaseUser user = getUsuarioAtual();

        if (user != null){

            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child( "usuarios" )
                    .child( getIdentificadorUsuario() );

            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue( Usuario.class );
                    String tipoUsuario = usuario.getTipo();

                    if (tipoUsuario.equals("M")){
                        activity.startActivity( new Intent(activity, RequisicoesActivity.class));

                    }else {
                        activity.startActivity( new Intent(activity, MapsActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
}
