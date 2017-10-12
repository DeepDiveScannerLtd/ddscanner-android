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
import com.ddscanner.interfaces.ListItemClickListener;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiveCentersListAdapterNew extends RecyclerView.Adapter<DiveCentersListAdapterNew.DiveCentersListViewHolder> {

    Context context;
    ArrayList<DiveCenter> diveCenters = new ArrayList<>();
    ListItemClickListener<DiveCenter> listItemClickListener;

    public DiveCentersListAdapterNew(ListItemClickListener<DiveCenter> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
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

    public void setListItemClickListener(ListItemClickListener<DiveCenter> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    class DiveCentersListViewHolder extends RecyclerView.ViewHolder {

        ImageView logo;
        TextView diveCenterName;
        TextView showDiveCenter;

        public DiveCentersListViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> listItemClickListener.onItemClick(diveCenters.get(getAdapterPosition())));
            context = itemView.getContext();
            logo = itemView.findViewById(R.id.logo);
            diveCenterName = itemView.findViewById(R.id.dc_name);
            showDiveCenter = itemView.findViewById(R.id.show_dc);
            showDiveCenter.setOnClickListener(view -> UserProfileActivity.show(context, diveCenters.get(getAdapterPosition()).getId(), 0));
        }

    }

}
