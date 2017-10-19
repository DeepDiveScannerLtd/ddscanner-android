package com.ddscanner.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.BaseIdNamePhotoEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseAdapterForEditProfile extends RecyclerView.Adapter<BaseAdapterForEditProfile.BaseRemovableItemViewHolder> {

    private ArrayList<BaseIdNamePhotoEntity> objects = new ArrayList<>();

    @Override
    public BaseRemovableItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_removable_tag, parent, false);
        return new BaseRemovableItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseRemovableItemViewHolder holder, int position) {
        holder.name.setText(objects.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void addAllobjects(ArrayList<BaseIdNamePhotoEntity> objects) {
        this.objects.addAll(objects);
        notifyDataSetChanged();
    }

    public void addObject(BaseIdNamePhotoEntity object) {
        this.objects.add(object);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.objects.remove(position);
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<BaseIdNamePhotoEntity> baseIdNamePhotoEntities) {
        this.objects = new ArrayList<>(baseIdNamePhotoEntities);
        notifyDataSetChanged();
    }

    public ArrayList<BaseIdNamePhotoEntity> getOjects() {
        return this.objects;
    }

    class BaseRemovableItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.dive_spot_name)
        TextView name;
        @BindView(R.id.ic_delete)
        ImageView delete;

        BaseRemovableItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            remove(getAdapterPosition());
        }
    }


}
