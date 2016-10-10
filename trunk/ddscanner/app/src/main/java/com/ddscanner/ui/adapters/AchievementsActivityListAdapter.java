package com.ddscanner.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.views.AchievementProgressView;

public class AchievementsActivityListAdapter extends RecyclerView.Adapter<AchievementsActivityListAdapter.AchievementActivityListViewHolder> {


    @Override
    public AchievementActivityListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_full_achievment, parent, false);
        return new AchievementActivityListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AchievementActivityListViewHolder holder, int position) {
        holder.progressView.setPercent(position / 10f);
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    class AchievementActivityListViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;
        protected TextView country;
        protected TextView progress;
        protected ImageView countryIcon;
        protected AchievementProgressView progressView;

        public AchievementActivityListViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            country = (TextView) view.findViewById(R.id.country);
            progress = (TextView) view.findViewById(R.id.progress);
            countryIcon = (ImageView) view.findViewById(R.id.country_flag);
            progressView = (AchievementProgressView) view.findViewById(R.id.progress_layout);

        }

    }

}
