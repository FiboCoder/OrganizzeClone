package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.R;

import java.util.List;

import Model.Movimentation;
public class MovimentationAdapter extends RecyclerView.Adapter<MovimentationAdapter.MyViewHolder> {

    List<Movimentation> movimentacoes;
    Context context;

    public MovimentationAdapter(List<Movimentation> movimentacoes, Context context) {
        this.movimentacoes = movimentacoes;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.movimentation_adapter, parent, false);
        return new MyViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movimentation movimentation = movimentacoes.get(position);

        holder.titulo.setText(movimentation.getDescription());
        holder.valor.setText(String.valueOf(movimentation.getValue()));
        holder.categoria.setText(movimentation.getCategory());
        holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccentReceitas));

        if (movimentation.getType().equals("E")) {
            holder.valor.setTextColor(context.getResources().getColor(R.color.accent));
            holder.valor.setText("-" + movimentation.getValue());
        }
    }


    @Override
    public int getItemCount() {
        return movimentacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, valor, categoria;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textAdapterTitulo);
            valor = itemView.findViewById(R.id.textAdapterValor);
            categoria = itemView.findViewById(R.id.textAdapterCategoria);
        }

    }

}
