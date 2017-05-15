package com.ddscanner.screens.achievements;

import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.ddscanner.entities.AchievementTitle;
import com.squareup.picasso.Picasso;

public class AchievementTitleViewModel {

    private AchievementTitle achievementTitle;

    public AchievementTitleViewModel(AchievementTitle achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

    public AchievementTitle getAchievementTitle() {
        return achievementTitle;
    }

    @BindingAdapter("loadImageFrom")
    public static void loadTitleIcon(ImageView view, AchievementTitleViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(viewModel.getAchievementTitle().getImage()).into(view);
        }
    }

    @BindingAdapter("loadDetails")
    public static void loadDetails(RecyclerView view, AchievementTitleViewModel viewModel) {
        if (viewModel != null) {
            view.setLayoutManager(new LinearLayoutManager(view.getContext()));
            view.setNestedScrollingEnabled(false);
            view.setAdapter(new AchievementTitleDetailsAdapter(viewModel.getAchievementTitle().getPointsToGoal(), viewModel.getAchievementTitle().getCountries()));
        }
    }

}
