package com.ddscanner.screens.divespots.list;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ProductItemBinding;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.events.ShowDIveSpotDetailsActivityEvent;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;

import java.util.ArrayList;

public class DiveSpotsListAdapter
        extends RecyclerView.Adapter<DiveSpotsListAdapter.ProductListViewHolder> {

    private static final String TAG = DiveSpotsListAdapter.class.getSimpleName();

    public ArrayList<DiveSpotShort> divespots;
    private Activity context;
    private EventsTracker.SpotViewSource spotViewSource;

    public DiveSpotsListAdapter(ArrayList<DiveSpotShort> divespots, Activity context, EventsTracker.SpotViewSource spotViewSource) {
        this.divespots = divespots;
        this.context = context;
        this.spotViewSource = spotViewSource;
    }

    public DiveSpotsListAdapter(Activity context) {
        this.context = context;
        this.spotViewSource = EventsTracker.SpotViewSource.FROM_LIST;
    }

    public void setData(ArrayList<DiveSpotShort> divespots) {
        this.divespots = divespots;
//        for (int i = 0; i < divespots.size(); i++) {
//            notifyItemInserted(i);
//        }
//        notifyDataSetChanged();
    }

    public void clearData() {
        if (divespots != null) {
            this.divespots.clear();
        }
//        this.divespots = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ProductItemBinding binding = ProductItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ProductListViewHolder(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(ProductListViewHolder productListViewHolder, int i) {
        productListViewHolder.binding.setViewModel(new DiveSpotListItemViewModel(divespots.get(productListViewHolder.getAdapterPosition())));
    }

    public void removeSpotFromList(int position) {
        divespots.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        if (divespots == null) {
            return 0;
        }
        return divespots.size();
    }

    public class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ProductItemBinding binding;

        public ProductListViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            binding = DataBindingUtil.bind(v);
        }

        @Override
        public void onClick(View v) {

            if (spotViewSource == EventsTracker.SpotViewSource.FROM_LIST) {
                DiveSpotDetailsActivity.show(context, String.valueOf(divespots.get(getAdapterPosition()).getId()),EventsTracker.SpotViewSource.FROM_LIST);
                return;
            }
            DDScannerApplication.bus.post(new ShowDIveSpotDetailsActivityEvent(getAdapterPosition(), String.valueOf(divespots.get(getAdapterPosition()).getId())));
//            DiveSpotDetailsActivity.showForResult(context, String.valueOf(divespots.get(getAdapterPosition()).getId()), spotViewSource, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOTS_LIST_ADAPTER);
        }
    }

}
