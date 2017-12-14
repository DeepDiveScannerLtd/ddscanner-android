package com.ddscanner.screens.profile.divecenter.fundives.list;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.ItemFunDiveBinding;
import com.ddscanner.entities.FunDive;
import com.ddscanner.interfaces.ListItemClickListener;

import java.util.ArrayList;

public class FunDivesListAdapter extends RecyclerView.Adapter<FunDivesListAdapter.FunDiveListItemViewHolder> {

    ArrayList<FunDive> funDives = new ArrayList<>();
    ListItemClickListener<FunDive> listItemClickListener;

    public FunDivesListAdapter(ListItemClickListener<FunDive> listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    public void setFunDives(ArrayList<FunDive> funDives) {
        this.funDives = funDives;
        notifyDataSetChanged();
    }

    @Override
    public FunDiveListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFunDiveBinding binding = ItemFunDiveBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FunDiveListItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(FunDiveListItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return funDives.size();
    }

    class FunDiveListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemFunDiveBinding binding;

        public FunDiveListItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onItemClick(funDives.get(getAdapterPosition()));
        }
    }

}
