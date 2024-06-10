package es.kingcreek.swifty_proteins.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.kingcreek.swifty_proteins.R;
import es.kingcreek.swifty_proteins.activities.ProteinView;
import es.kingcreek.swifty_proteins.interfaces.AdapterHandler;

public class ProteinAdapter extends RecyclerView.Adapter<ProteinAdapter.ViewHolder> {

    private List<String> proteins;
    private List<String> filteredProteins;
    private Context context;
    private AdapterHandler callback;

    public ProteinAdapter(Context context, AdapterHandler callback, List<String> proteins, List<String> filteredProteins) {
        this.context = context;
        this.proteins = proteins;
        this.filteredProteins = filteredProteins;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_protein, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String protein = filteredProteins.get(position);
        holder.protein.setText(protein);
    }

    @Override
    public int getItemCount() {
        return filteredProteins.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView protein;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            protein = itemView.findViewById(R.id.proteinText);
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String protein = filteredProteins.get(getBindingAdapterPosition());
            callback.onItemClicked(protein);
        }
    }

    // Search filter
    public void filter(String query) {
        filteredProteins.clear();
        // If search are empty
        if (TextUtils.isEmpty(query)) {
            filteredProteins.addAll(proteins);
        } else {
            // Filter
            for (String p : proteins) {
                if (p != null) {
                    if (p.toLowerCase().contains(query.toLowerCase())) {
                        filteredProteins.add(p);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
