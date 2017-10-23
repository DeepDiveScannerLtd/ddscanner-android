package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

public class SealifeListAddingDiveSpotAdapter extends RecyclerView.Adapter<SealifeListAddingDiveSpotAdapter.SealifeListAddingDivespotViewHolder>{

    private Context context;
    private ArrayList<SealifeShort> sealifes = new ArrayList<>();

    public SealifeListAddingDiveSpotAdapter(ArrayList<SealifeShort> sealifes, Context context) {
        this.sealifes = sealifes;
        this.context = context;
    }

    public SealifeListAddingDiveSpotAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SealifeListAddingDivespotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_removable_tag, parent, false);
        return new SealifeListAddingDivespotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SealifeListAddingDivespotViewHolder holder, final int position) {
        holder.sealifeName.setText(sealifes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (sealifes == null) {
            sealifes = new ArrayList<>();
            return 0;
        }
        return sealifes.size();
    }

    public void add(SealifeShort sealife) {
        if (Helpers.checkIsSealifeAlsoInList(sealifes, sealife.getId())) {
            return;
        }
        sealifes.add(sealife);
        notifyDataSetChanged();
    }

    public void addSealifesList(ArrayList<SealifeShort> sealifes) {
        this.sealifes.addAll(sealifes);
        notifyDataSetChanged();
    }

    public void deleteSealife(int position) {
        this.sealifes.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<SealifeShort> getSealifes() {
        if (this.sealifes == null) {
            return new ArrayList<>();
        }
        return this.sealifes;
    }

    public ArrayList<String> getSealifesIds() {
        if (this.sealifes.size() == 0) {
            return null;
        }
        ArrayList<String> ids = new ArrayList<>();
        for (SealifeShort sealifeShort : this.sealifes) {
            ids.add(sealifeShort.getId());
        }
        return ids;
    }

    public class SealifeListAddingDivespotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView deleteButton;
        protected TextView sealifeName;

        public SealifeListAddingDivespotViewHolder(View v) {
            super(v);
            deleteButton = v.findViewById(R.id.ic_delete);
            sealifeName = v.findViewById(R.id.dive_spot_name);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            deleteSealife(getAdapterPosition());
        }
    }

}
