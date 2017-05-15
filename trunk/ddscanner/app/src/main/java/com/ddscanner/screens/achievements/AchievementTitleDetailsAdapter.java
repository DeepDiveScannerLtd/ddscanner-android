package com.ddscanner.screens.achievements;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.databinding.ItemAchivementTitleDetailsBinding;
import com.ddscanner.entities.AchievementTitleDetails;

import java.util.ArrayList;

public class AchievementTitleDetailsAdapter extends RecyclerView.Adapter<AchievementTitleDetailsAdapter.AchievementTitleDetailsViewHolder>{

    private ArrayList<AchievementTitleDetails> achievementTitleDetailses = new ArrayList<>();
    private int goalCount;

    public AchievementTitleDetailsAdapter(int goalCount, ArrayList<AchievementTitleDetails> achievementTitleDetailses) {
        this.goalCount = goalCount;
        this.achievementTitleDetailses = achievementTitleDetailses;
    }

    @Override
    public AchievementTitleDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAchivementTitleDetailsBinding binding = ItemAchivementTitleDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AchievementTitleDetailsViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(AchievementTitleDetailsViewHolder holder, int position) {
        holder.binding.setViewModel(new AchievementTitleDetailsViewModel(achievementTitleDetailses.get(position), goalCount));
    }

    @Override
    public int getItemCount() {
        return achievementTitleDetailses.size();
    }

    class AchievementTitleDetailsViewHolder extends RecyclerView.ViewHolder {

        private ItemAchivementTitleDetailsBinding binding;

        AchievementTitleDetailsViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

    }
}
