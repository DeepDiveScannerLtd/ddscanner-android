package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;

import java.util.ArrayList;

public class DiveSpotsListForEditDcAdapter extends RecyclerView.Adapter<DiveSpotsListForEditDcAdapter.DiveSpotViewHolder> {

    private ArrayList<DiveSpotShort> diveSpots = new ArrayList<>();

    @Override
    public DiveSpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dive_spot_dc_profile, parent, false);
        return new DiveSpotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiveSpotViewHolder holder, int position) {
        holder.diveSpotName.setText(diveSpots.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return diveSpots.size();
    }

    public void addAll(ArrayList<DiveSpotShort> diveSpots) {
        this.diveSpots.addAll(diveSpots);
        notifyDataSetChanged();
    }

    public void add(DiveSpotShort diveSpotShort) {
        this.diveSpots.add(diveSpotShort);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.diveSpots.remove(position);
        notifyDataSetChanged();
    }

    class DiveSpotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView diveSpotName;
        private ImageView diveSpotDelete;

        public DiveSpotViewHolder(View view) {
            super(view);
            diveSpotName = (TextView) view.findViewById(R.id.dive_spot_name);
            diveSpotDelete = (ImageView) view.findViewById(R.id.ic_delete);
            diveSpotDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            remove(getAdapterPosition());
        }
    }

}
