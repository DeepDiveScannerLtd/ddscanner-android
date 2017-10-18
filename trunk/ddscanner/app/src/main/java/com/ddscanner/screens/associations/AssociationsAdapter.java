package com.ddscanner.screens.associations;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.Association;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssociationsAdapter extends RecyclerView.Adapter<AssociationsAdapter.AssociationViewHolder> {

    ArrayList<Association> associations = new ArrayList<>();

    @Override
    public AssociationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_search_base, parent, false);
        return new AssociationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AssociationViewHolder holder, int position) {
        holder.name.setText(associations.get(position).getName());
        if (associations.get(position).isChecked()) {
            holder.checkingIcon.setVisibility(View.VISIBLE);
        } else {
            holder.checkingIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class AssociationViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.language_name)
        TextView name;
        @BindView(R.id.checking_icon)
        ImageView checkingIcon;

        public AssociationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
