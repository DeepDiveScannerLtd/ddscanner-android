package com.ddscanner.screens.booking.offers.dailytours;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.ItemOfferBinding;
import com.ddscanner.databinding.ItemOffersDivecenterBinding;
import com.ddscanner.entities.DiveCenterShort;
import com.ddscanner.entities.Offer;

import java.util.ArrayList;

public class DailyToursListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int OFFER_HOLDER_TYPE = 0;
    private static final int DIVIDER_HOLDER_TYPE = 1;
    private static final int DIVECENTER_HOLDER_TYPE = 2;
    private ArrayList<Offer> offers = new ArrayList<>();
    private ArrayList<DiveCenterShort> diveCenters = new ArrayList<>();


    public DailyToursListAdapter(ArrayList<Offer> offers, ArrayList<DiveCenterShort> diveCenters) {
        this.offers = offers;
        this.diveCenters = diveCenters;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DIVECENTER_HOLDER_TYPE:
                ItemOffersDivecenterBinding divecenterBinding = ItemOffersDivecenterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DiveCenterViewHolder(divecenterBinding.getRoot());
            case OFFER_HOLDER_TYPE:
                ItemOfferBinding binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new OffersListViewHolder(binding.getRoot());
            case DIVIDER_HOLDER_TYPE:
                View dividerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offers_list_divider, parent, false);
                return new DiveCentersDividerViewHolder(dividerView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case DIVIDER_HOLDER_TYPE:
                break;
            case OFFER_HOLDER_TYPE:
                OffersListViewHolder offersListViewHolder = (OffersListViewHolder) holder;
                offersListViewHolder.binding.setOfferViewModel(new DailyToursListItemViewModel(offers.get(position)));
                break;
            case DIVECENTER_HOLDER_TYPE:
                DiveCenterViewHolder diveCenterViewHolder = (DiveCenterViewHolder) holder;
                diveCenterViewHolder.binding.setViewModel(new DiveCenterListItemViewModel(diveCenters.get(position - offers.size() - 1)));
                break;
        }
//        holder.binding.setOfferViewModel(new OfferListItemViewModel(offers.get(position)));
    }

    @Override
    public int getItemCount() {
        return offers.size() + 1 + diveCenters.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < offers.size()) {
            return OFFER_HOLDER_TYPE;
        }
        if (position == offers.size()) {
            return DIVIDER_HOLDER_TYPE;
        }
        return DIVECENTER_HOLDER_TYPE;
    }

    class OffersListViewHolder extends RecyclerView.ViewHolder {

        ItemOfferBinding binding;

        OffersListViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }

    class DiveCentersDividerViewHolder extends RecyclerView.ViewHolder {

        DiveCentersDividerViewHolder(View view) {
            super(view);
        }

    }

    class DiveCenterViewHolder extends RecyclerView.ViewHolder {

        ItemOffersDivecenterBinding binding;

        DiveCenterViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }

}
