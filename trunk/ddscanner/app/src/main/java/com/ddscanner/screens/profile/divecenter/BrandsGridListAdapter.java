package com.ddscanner.screens.profile.divecenter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ddscanner.R;
import com.ddscanner.entities.Brand;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BrandsGridListAdapter  extends RecyclerView.Adapter<BrandsGridListAdapter.BrandsImageViewHolder> {

    ArrayList<Brand> brands = new ArrayList<>();
    Context context;

    @Override
    public BrandsImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_image, parent, false);
        return new BrandsImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BrandsImageViewHolder holder, int position) {
        Picasso.with(context).load(brands.get(position).getLogo()).into(holder.view);
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public void setBrands(ArrayList<Brand> brands) {
        this.brands = brands;
        notifyDataSetChanged();
    }

    class BrandsImageViewHolder extends RecyclerView.ViewHolder {

        ImageView view;

        public BrandsImageViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.image);
        }
    }

}
