package com.ddscanner.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.CompleteAchievement;
import com.ddscanner.entities.Countries;
import com.ddscanner.entities.PendingAchievement;
import com.ddscanner.ui.views.AchievementCountryFlagView;
import com.ddscanner.ui.views.AchievementProgressView;
import com.ddscanner.utils.Helpers;

import java.util.ArrayList;

public class AchievementsActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = AchievementsActivityListAdapter.class.getSimpleName();

    private final int COMPLETE_ACHIEVEMNT_INDEX = 0;
    private final int PENDING_ACHIEVEMNT_INDEX = 1;

    private ArrayList<? extends CompleteAchievement> achievements;
    private Context context;
    private Countries countries;

    public AchievementsActivityListAdapter(ArrayList<CompleteAchievement> achievements, Context context) {
        this.achievements = achievements;
        this.context = context;

        countries = Helpers.getCountries();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case COMPLETE_ACHIEVEMNT_INDEX:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complete_achievement, parent, false);
                return new CompleteAchievementViewHolder(itemView);
            case PENDING_ACHIEVEMNT_INDEX:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_achievment, parent, false);
                return new PendingAchievementViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case COMPLETE_ACHIEVEMNT_INDEX:
                CompleteAchievementViewHolder completeAchievementViewHolder = (CompleteAchievementViewHolder) holder;
                if (!achievements.get(position).getCountry().isEmpty() && !achievements.get(position).getCountry().equals("NT")) {
                    try {
                        completeAchievementViewHolder.country.setText(countries.getCountry(achievements.get(position).getCountry()).getCountryName());
                        completeAchievementViewHolder.countryIcon.setFlagBitmap(Helpers.getResId(achievements.get(position).getCountry().toLowerCase(), R.drawable.class));
                    } catch (NullPointerException e) {

                    } catch (Exception e) {

                    }
                }
                completeAchievementViewHolder.type.setText(achievements.get(position).getName());
                completeAchievementViewHolder.progress.setText(achievements.get(position).getPoints());
                break;
            case PENDING_ACHIEVEMNT_INDEX:
                PendingAchievementViewHolder pendingAchievementViewHolder = (PendingAchievementViewHolder) holder;
                PendingAchievement pendingAchievement = (PendingAchievement) achievements.get(position);
                pendingAchievementViewHolder.progressView.setPercent(Float.valueOf(pendingAchievement.getProgress()) / Float.valueOf(pendingAchievement.getPoints()));
                pendingAchievementViewHolder.progress.setText(pendingAchievement.getProgress() + "/" + pendingAchievement.getPoints());
                if (!pendingAchievement.getCountry().isEmpty() && !achievements.get(position).getCountry().equals("NT")) {
                    Log.i(TAG, pendingAchievement.getCountry());
                    try {
                        pendingAchievementViewHolder.countryIcon.setFlagBitmap(Helpers.getResId(pendingAchievement.getCountry().toLowerCase(), R.drawable.class));
                        pendingAchievementViewHolder.country.setText(countries.getCountry(pendingAchievement.getCountry()).getCountryName());
                    } catch (Exception e) {

                    }
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
        protected AchievementCountryFlagView countryIcon;
        protected AchievementProgressView progressView;

        public PendingAchievementViewHolder(View view) {
            super(view);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Math.round(Helpers.convertDpToPixel(33, context)), Math.round(Helpers.convertDpToPixel(33, context)));
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            type = (TextView) view.findViewById(R.id.type);
            country = (TextView) view.findViewById(R.id.country);
            progress = (TextView) view.findViewById(R.id.progress);
            countryIcon = (AchievementCountryFlagView) view.findViewById(R.id.country_flag);
            progressView = (AchievementProgressView) view.findViewById(R.id.progress_layout);
            countryIcon.setLayoutParams(layoutParams);

        }

    }

    class CompleteAchievementViewHolder extends RecyclerView.ViewHolder {

        protected TextView type;
        protected TextView country;
        protected TextView progress;
        protected AchievementCountryFlagView countryIcon;

        public CompleteAchievementViewHolder(View view) {
            super(view);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Math.round(Helpers.convertDpToPixel(33, context)), Math.round(Helpers.convertDpToPixel(33, context)));
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            type = (TextView) view.findViewById(R.id.type);
            country = (TextView) view.findViewById(R.id.country);
            progress = (TextView) view.findViewById(R.id.progress);
            countryIcon = (AchievementCountryFlagView) view.findViewById(R.id.country_flag);
            countryIcon.setLayoutParams(layoutParams);
        }
    }

}
