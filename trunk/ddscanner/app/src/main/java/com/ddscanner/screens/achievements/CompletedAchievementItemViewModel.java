package com.ddscanner.screens.achievements;

import android.databinding.BindingAdapter;

import com.ddscanner.R;
import com.ddscanner.entities.CompleteAchievement;
import com.ddscanner.ui.views.AchievementCountryFlagView;
import com.ddscanner.utils.Helpers;

public class CompletedAchievementItemViewModel {

    private CompleteAchievement completeAchievement;

    public CompletedAchievementItemViewModel(CompleteAchievement completeAchievement) {
        this.completeAchievement = completeAchievement;
    }

    public CompleteAchievement getCompleteAchievement() {
        return completeAchievement;
    }

    @BindingAdapter("loadCountryFlagFrom")
    public static void loadCountryFlag(final AchievementCountryFlagView view, CompletedAchievementItemViewModel viewModel) {
        if (viewModel != null) {
            view.setFlagBitmap(Helpers.getResId(viewModel.getCompleteAchievement().getCountry().getCode().toLowerCase(), R.drawable.class));
        }
    }

}
