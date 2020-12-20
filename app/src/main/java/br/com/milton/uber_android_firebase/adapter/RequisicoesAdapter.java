package br.com.milton.uber_android_firebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.milton.uber_android_firebase.R;
import br.com.milton.uber_android_firebase.model.Requisicao;
import br.com.milton.uber_android_firebase.model.Usuario;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List<Requisicao> requisicoes;
    private Context context;
    private Usuario motorista;

    public RequisicoesAdapter(List<Requisicao> requisicoes, Context context, Usuario motorista) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.motorista = motorista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requisicoes, parent, false);
        return new MyViewHolder( item );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Requisicao requisicao = requisicoes.get( position );
        Usuario passageiro = requisicao.getPassageiro();

        holder.nome.setText( passageiro.getNome() );
        holder.distancia.setText( "1 km - Aproximadamente" );

    }

    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, distancia;

        public MyViewHolder(View itemView){
            super(itemView);

            nome = itemView.findViewById(R.id.txtRequisicaoNome);
            distancia = itemView.findViewById(R.id.txtRequisicaoDistancia);
        }
    }
}
