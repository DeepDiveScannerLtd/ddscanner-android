package com.ddscanner.screens.boocking.offers;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemOfferBinding;
import com.ddscanner.entities.Offer;

import java.util.ArrayList;

public class OffersListAdapter extends RecyclerView.Adapter<OffersListAdapter.OffersListViewHolder> {

    private ArrayList<Offer> offers = new ArrayList<>();

    public OffersListAdapter(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    @Override
    public OffersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemOfferBinding binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OffersListViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(OffersListViewHolder holder, int position) {
        holder.binding.setOfferViewModel(new OfferListItemViewModel(offers.get(position)));
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class OffersListViewHolder extends RecyclerView.ViewHolder {

        ItemOfferBinding binding;

        OffersListViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }

}
