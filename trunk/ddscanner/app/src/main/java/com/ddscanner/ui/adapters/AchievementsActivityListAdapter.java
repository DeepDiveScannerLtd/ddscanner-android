package com.ddscanner.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.CompleteAchievement;
import com.ddscanner.entities.PendingAchievement;
import com.ddscanner.ui.views.AchievementProgressView;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AchievementsActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int COMPLETE_ACHIEVEMNT_INDEX = 0;
    private final int PENDING_ACHIEVEMNT_INDEX = 1;

    private ArrayList<? extends CompleteAchievement> achievements;
    private Context context;

    public AchievementsActivityListAdapter(ArrayList<CompleteAchievement> achievements, Context context) {
        this.achievements = achievements;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case COMPLETE_ACHIEVEMNT_INDEX:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complete_achievement, parent, false);
                return new CompleteAchievementViewHolder(itemView);
            case PENDING_ACHIEVEMNT_INDEX:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_full_achievment, parent, false);
                return new PendingAchievementViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case COMPLETE_ACHIEVEMNT_INDEX:
                CompleteAchievementViewHolder completeAchievementViewHolder = (CompleteAchievementViewHolder) holder;
                if (!achievements.get(position).getCountry().isEmpty()) {
                    completeAchievementViewHolder.country.setText(Helpers.getCountries().getCountry(achievements.get(position).getCountry()).getCountryName());
                    completeAchievementViewHolder.countryIcon.setImageDrawable(ContextCompat.getDrawable(context, Helpers.getResId(achievements.get(position).getCountry().toLowerCase(), R.drawable.class)));
                }
                completeAchievementViewHolder.type.setText(achievements.get(position).getName());
                completeAchievementViewHolder.progress.setText(achievements.get(position).getPoints());
                break;
            case PENDING_ACHIEVEMNT_INDEX:
                PendingAchievementViewHolder pendingAchievementViewHolder = (PendingAchievementViewHolder) holder;
                PendingAchievement pendingAchievement = (PendingAchievement) achievements.get(position);
                pendingAchievementViewHolder.progressView.setPercent(Float.valueOf(pendingAchievement.getProgress()) / Float.valueOf(pendingAchievement.getPoints()));
                pendingAchievementViewHolder.progress.setText(pendingAchievement.getProgress() + "/" + pendingAchievement.getPoints());
                if (!pendingAchievement.getCountry().isEmpty()) {
                    pendingAchievementViewHolder.country.setText(Helpers.getCountries().getCountry(pendingAchievement.getCountry()).getCountryName());
                    pendingAchievementViewHolder.countryIcon.setImageDrawable(ContextCompat.getDrawable(context, Helpers.getResId(pendingAchievement.getCountry().toLowerCase(), R.drawable.class)));
                }
                pendingAchievementViewHolder.type.setText(pendingAchievement.getName());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (achievements.get(position) instanceof PendingAchievement) {
            return PENDING_ACHIEVEMNT_INDEX;
        }
        return COMPLETE_ACHIEVEMNT_INDEX;
    }

    @Override
    public int getItemCount() {
        if (achievements == null) {
            return 0;
        }
        return achievements.size();
    }

    class PendingAchievementViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;
        protected TextView country;
        protected TextView progress;
        protected ImageView countryIcon;
        protected AchievementProgressView progressView;

        public PendingAchievementViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            country = (TextView) view.findViewById(R.id.country);
            progress = (TextView) view.findViewById(R.id.progress);
            countryIcon = (ImageView) view.findViewById(R.id.country_flag);
            progressView = (AchievementProgressView) view.findViewById(R.id.progress_layout);

        }

    }

    class CompleteAchievementViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;
        protected TextView country;
        protected TextView progress;
        protected CircleImageView countryIcon;

        public CompleteAchievementViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            country = (TextView) view.findViewById(R.id.country);
            progress = (TextView) view.findViewById(R.id.progress);
            countryIcon = (CircleImageView) view.findViewById(R.id.country_flag);
        }
    }

}
