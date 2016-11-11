package com.ddscanner.screens.achievements;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ddscanner.databinding.ItemCompleteAchievementBinding;
import com.ddscanner.databinding.ItemPendingAchievmentBinding;
import com.ddscanner.entities.CompleteAchievement;
import com.ddscanner.entities.Countries;
import com.ddscanner.entities.PendingAchievement;
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
                LayoutInflater completeInflater = LayoutInflater.from(parent.getContext());
                ItemCompleteAchievementBinding completeAchievementBinding = ItemCompleteAchievementBinding.inflate(completeInflater, parent, false);
                return new CompleteAchievementViewHolder(completeAchievementBinding.getRoot());
            case PENDING_ACHIEVEMNT_INDEX:
                LayoutInflater pendingInflater = LayoutInflater.from(parent.getContext());
                ItemPendingAchievmentBinding pendingAchievmentBinding = ItemPendingAchievmentBinding.inflate(pendingInflater, parent, false);
                return new PendingAchievementViewHolder(pendingAchievmentBinding.getRoot());
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case COMPLETE_ACHIEVEMNT_INDEX:
                CompleteAchievementViewHolder completeAchievementViewHolder = (CompleteAchievementViewHolder) holder;
                completeAchievementViewHolder.binding.setAchievementViewModel(new CompletedAchievementItemViewModel(achievements.get(position)));
                break;
            case PENDING_ACHIEVEMNT_INDEX:
                PendingAchievementViewHolder pendingAchievementViewHolder = (PendingAchievementViewHolder) holder;
                pendingAchievementViewHolder.binding.setAchievementViewModel(new PendingAchievementItemViewModel((PendingAchievement) achievements.get(position)));
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

        ItemPendingAchievmentBinding binding;

        public PendingAchievementViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Math.round(Helpers.convertDpToPixel(33, context)), Math.round(Helpers.convertDpToPixel(33, context)));
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            binding.countryFlag.setLayoutParams(layoutParams);

        }

    }

    class CompleteAchievementViewHolder extends RecyclerView.ViewHolder {

        ItemCompleteAchievementBinding binding;

        public CompleteAchievementViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Math.round(Helpers.convertDpToPixel(33, context)), Math.round(Helpers.convertDpToPixel(33, context)));
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            binding.countryFlag.setLayoutParams(layoutParams);
        }
    }

}
