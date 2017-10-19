package com.ddscanner.screens.brands;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Association;
import com.ddscanner.entities.Brand;
import com.ddscanner.interfaces.ListItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.BrandViewHolder> {

    ArrayList<Brand> brands = new ArrayList<>();
    Context context;
    ListItemClickListener<Brand> listItemClickListener;

    @Override
    public BrandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_brand, parent, false);
        return new BrandViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BrandViewHolder holder, int position) {
        holder.name.setText(brands.get(position).getName());
        Picasso.with(context).load(brands.get(position).getLogo()).into(holder.logo);
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public void setBrands(ArrayList<Brand> brands) {
        this.brands = brands;
        notifyDataSetChanged();
    }

    public void setListItemClickListener(ListItemClickListener<Brand> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    class BrandViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.brand_name)
        TextView name;
        @BindView(R.id.logo)
        ImageView logo;

        public BrandViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listItemClickListener != null) {
                listItemClickListener.onItemClick(brands.get(getAdapterPosition()));
            }
        }
    }

}
