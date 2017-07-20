package com.ddscanner.screens.achievements;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemAchievementTitleBinding;
import com.ddscanner.entities.AchievementTitle;

import java.util.ArrayList;

public class AchievementTitleListAdapter extends RecyclerView.Adapter<AchievementTitleListAdapter.AchievementTitleItemViewHolder> {

    private ArrayList<AchievementTitle> achievementTitles = new ArrayList<>();

    @Override
    public AchievementTitleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAchievementTitleBinding binding = ItemAchievementTitleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AchievementTitleItemViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(AchievementTitleItemViewHolder holder, int position) {
        holder.binding.setViewModel(new AchievementTitleViewModel(achievementTitles.get(position)));
    }

    @Override
    public int getItemCount() {
        return achievementTitles.size();
    }

    public void setAchievementTitles(ArrayList<AchievementTitle> achievementTitles) {
        this.achievementTitles = achievementTitles;
    }

    class AchievementTitleItemViewHolder extends RecyclerView.ViewHolder {

        private ItemAchievementTitleBinding binding;

        AchievementTitleItemViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }

}
