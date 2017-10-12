package com.ddscanner.screens.diecenters.list;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiveCentersListAdapterNew extends RecyclerView.Adapter<DiveCentersListAdapterNew.DiveCentersListViewHolder> {

    Context context;
    ArrayList<DiveCenter> diveCenters = new ArrayList<>();

    public DiveCentersListAdapterNew() {
    }

    @Override
    public DiveCentersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_dive_center, parent, false);
        return new DiveCentersListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DiveCentersListViewHolder holder, int position) {
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, diveCenters.get(position).getLogo(), "1")).into(holder.logo);
        holder.diveCenterName.setText(diveCenters.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return diveCenters.size();
    }

    public void setData(ArrayList<DiveCenter> diveCenters) {
        this.diveCenters = diveCenters;
        notifyDataSetChanged();
    }

    class DiveCentersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView logo;
        TextView diveCenterName;
        TextView showDiveCenter;

        public DiveCentersListViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            logo = itemView.findViewById(R.id.logo);
            diveCenterName = itemView.findViewById(R.id.dc_name);
            showDiveCenter = itemView.findViewById(R.id.show_dc);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
