package com.ddscanner.screens.divespots.map;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.BaseMapEntity;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiveSpotsMapDiveCenterListAdapter extends RecyclerView.Adapter<DiveSpotsMapDiveCenterListAdapter.DiveCenterMapListItemViewHolder> {

    private ArrayList<BaseMapEntity> list = new ArrayList<>();
    private Activity context;

    public void setList(ArrayList<BaseMapEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public DiveSpotsMapDiveCenterListAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public DiveCenterMapListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiveCenterMapListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_divecenter_list_mapfragment, parent, false));
    }

    @Override
    public void onBindViewHolder(DiveCenterMapListItemViewHolder holder, int position) {
        holder.dcName.setText(list.get(position).getName());
        holder.dcAddress.setText(list.get(position).getAddresses().get(0).getName());
        Picasso.with(context).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, list.get(position).getPhoto(), "1")).placeholder(R.drawable.avatar_dc_profile_def).into(holder.dcLogo);
        holder.dcLanguages.setText(list.get(position).getLanguagesString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DiveCenterMapListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView dcName;
        private TextView dcAddress;
        private TextView dcLanguages;
        private ImageView dcLogo;

        public DiveCenterMapListItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            dcAddress = itemView.findViewById(R.id.dc_address);
            dcName = itemView.findViewById(R.id.dc_name);
            dcLanguages = itemView.findViewById(R.id.dc_languages);
            dcLogo = itemView.findViewById(R.id.dc_logo);
        }

        @Override
        public void onClick(View v) {
            UserProfileActivity.show(context, String.valueOf(list.get(getAdapterPosition()).getId()), 0);
        }
    }

}
