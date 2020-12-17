package br.com.milton.uber_android_firebase.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import br.com.milton.uber_android_firebase.config.ConfiguracaoFirebase;

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
}
