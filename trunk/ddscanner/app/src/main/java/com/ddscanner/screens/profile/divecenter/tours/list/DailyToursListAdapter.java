package com.ddscanner.screens.profile.divecenter.tours.list;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemDailyTourBinding;
import com.ddscanner.entities.DailyTour;
import com.ddscanner.interfaces.ListItemClickListener;

public class DailyToursListAdapter extends RecyclerView.Adapter<DailyToursListAdapter.DailyTourItemViewHolder> {

    private DailyTour dailyTour = new DailyTour();
    private ListItemClickListener<DailyTour> dailyTourListItemClickListener;

    public DailyToursListAdapter(ListItemClickListener<DailyTour> dailyTourListItemClickListener) {
        this.dailyTourListItemClickListener = dailyTourListItemClickListener;
    }

    @Override
    public DailyTourItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDailyTourBinding itemDailyTourBinding = ItemDailyTourBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new DailyTourItemViewHolder(itemDailyTourBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(DailyTourItemViewHolder holder, int position) {
        holder.binding.setViewModel(new DailyTourListItemViewModel(dailyTour));
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class DailyTourItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemDailyTourBinding binding;

        public DailyTourItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void onClick(View v) {
            dailyTourListItemClickListener.onItemClick(dailyTour);
        }
    }

}
