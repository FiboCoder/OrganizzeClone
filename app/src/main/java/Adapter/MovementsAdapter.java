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
public class MovementsAdapter extends RecyclerView.Adapter<MovementsAdapter.MyViewHolder> {

    List<Movimentation> movementsList;
    Context context;

    public MovementsAdapter(List<Movimentation> movements, Context context) {
        this.movementsList = movements;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.movimentation_adapter, parent, false);
        return new MyViewHolder(itemList);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Movimentation movimentation = movementsList.get(position);

        holder.title.setText(movimentation.getDescription());
        holder.value.setText(String.valueOf("R$" + movimentation.getValue()));
        holder.category.setText(movimentation.getCategory());
        holder.value.setTextColor(context.getResources().getColor(R.color.colorAccentReceitas));

        if (movimentation.getType().equals("E")) {
            holder.value.setTextColor(context.getResources().getColor(R.color.accent));
            holder.value.setText("-R$" + movimentation.getValue());
        }
    }


    @Override
    public int getItemCount() {
        return movementsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, value, category;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textAdapterTitulo);
            value = itemView.findViewById(R.id.textAdapterValor);
            category = itemView.findViewById(R.id.textAdapterCategoria);
        }

    }

}
