package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Language;

import java.util.ArrayList;

public class DiveCenterLanguagesListAdapter extends RecyclerView.Adapter<DiveCenterLanguagesListAdapter.LanguagesViewHolder> {

    private ArrayList<Language> objects = new ArrayList<>();

    @Override
    public LanguagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dive_spot_dc_profile, parent, false);
        return new LanguagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LanguagesViewHolder holder, int position) {
        holder.name.setText(objects.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void addAllLanguages(ArrayList<Language> objects) {
        this.objects.addAll(objects);
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

    public ArrayList<Language> getObjects() {
        return this.objects;
    }

    class LanguagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private ImageView delete;

        public LanguagesViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.dive_spot_name);
            delete = view.findViewById(R.id.ic_delete);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            remove(getAdapterPosition());
        }
    }

}

