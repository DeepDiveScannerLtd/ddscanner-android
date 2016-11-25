package com.ddscanner.screens.achievements;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.ddscanner.entities.CompleteAchievement;
import com.ddscanner.ui.views.AchievementCountryFlagView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                view.setFlagBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        //TODO change to image address
        Picasso.with(view.getContext()).load(viewModel.getCompleteAchievement().getCountry()).into(target);
    }

}
