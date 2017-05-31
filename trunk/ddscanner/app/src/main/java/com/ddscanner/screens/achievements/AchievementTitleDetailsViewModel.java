package com.ddscanner.screens.achievements;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.AchievementTitleDetails;
import com.ddscanner.ui.views.AchievementLineProgresView;
import com.ddscanner.utils.Helpers;

import de.hdodenhof.circleimageview.CircleImageView;

public class AchievementTitleDetailsViewModel {

    private int goalPoints;
    private AchievementTitleDetails achievementTitleDetails;

    public AchievementTitleDetailsViewModel(AchievementTitleDetails achievementTitleDetails, int goalPoints) {
        this.achievementTitleDetails = achievementTitleDetails;
        this.goalPoints = goalPoints;
    }

    public AchievementTitleDetails getAchievementTitleDetails() {
        return achievementTitleDetails;
    }

    @BindingAdapter("bind:loadCountryFlag")
    public static void setCountryFlag(CircleImageView view, AchievementTitleDetailsViewModel viewModel) {
        if (viewModel != null) {
            try {
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), Helpers.getResId(viewModel.getAchievementTitleDetails().getCountry().getCode().toLowerCase(), R.drawable.class)));
            } catch (Exception ignored) {

            }
        }
    }

    @BindingAdapter("setProgress")
    public static void setProgress(AchievementLineProgresView view, AchievementTitleDetailsViewModel viewModel) {
        if (viewModel != null) {
            view.setProgress((float)viewModel.getAchievementTitleDetails().getProgress() / viewModel.goalPoints);
        }
    }

    @BindingAdapter("setProgressText")
    public static void setProgressText(TextView view, AchievementTitleDetailsViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel.getAchievementTitleDetails().getProgress() < viewModel.goalPoints) {
                view.setText(viewModel.getAchievementTitleDetails().getProgress() + "/" + viewModel.goalPoints);
            } else {
                view.setText(String.valueOf(viewModel.goalPoints));
            }
        }
    }

}

}
