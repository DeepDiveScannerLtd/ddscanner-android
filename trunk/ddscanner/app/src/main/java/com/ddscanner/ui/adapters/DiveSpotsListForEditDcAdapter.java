package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.Language;

import java.util.ArrayList;
import java.util.List;

public class DiveSpotsListForEditDcAdapter extends RecyclerView.Adapter<DiveSpotsListForEditDcAdapter.DiveSpotViewHolder> {

    private ArrayList<Object> objects = new ArrayList<>();

    @Override
    public DiveSpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dive_spot_dc_profile, parent, false);
        return new DiveSpotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiveSpotViewHolder holder, int position) {
        DiveSpotShort diveSpotShort;
        Language language;
        if (objects.get(position) instanceof DiveSpotShort) {
            diveSpotShort = (DiveSpotShort) objects.get(position);
            holder.name.setText(diveSpotShort.getName());
        }
        if (objects.get(position) instanceof Language) {
            language = (Language) objects.get(position);
            holder.name.setText(language.getName());
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void addAllDiveSpots(ArrayList<DiveSpotShort> objects) {
        this.objects.addAll(objects);
        notifyDataSetChanged();
    }

    public void addAllLanguages(ArrayList<Language> objects) {
        this.objects.addAll(objects);
        notifyDataSetChanged();
    }

    public void addDiveSpot(DiveSpotShort object) {
        this.objects.add(object);
        notifyDataSetChanged();
    }

    public void addLanguage(Language object) {
        this.objects.add(object);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.objects.remove(position);
        notifyDataSetChanged();
    }

    public List<Object> getObjects() {
        return this.objects;
    }

    class DiveSpotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private ImageView delete;

        public DiveSpotViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.dive_spot_name);
            delete = (ImageView) view.findViewById(R.id.ic_delete);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            remove(getAdapterPosition());
        }
    }

}
