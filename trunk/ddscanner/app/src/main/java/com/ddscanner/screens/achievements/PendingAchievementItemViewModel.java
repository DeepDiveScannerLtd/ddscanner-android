package com.ddscanner.screens.achievements;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.entities.PendingAchievement;
import com.ddscanner.ui.views.AchievementCountryFlagView;
import com.ddscanner.ui.views.AchievementProgressView;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PendingAchievementItemViewModel {

    private PendingAchievement pendingAchievement;

    public PendingAchievementItemViewModel(PendingAchievement pendingAchievement) {
        this.pendingAchievement = pendingAchievement;
    }

    public PendingAchievement getPendingAchievement() {
        return pendingAchievement;
    }

    @BindingAdapter("loadCountryFlagFrom")
    public static void loadCountryFlag(final AchievementCountryFlagView view, PendingAchievementItemViewModel viewModel) {
        if (viewModel != null) {
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
            view.setFlagBitmap(Helpers.getResId(viewModel.getPendingAchievement().getCountry().toLowerCase(), R.drawable.class));
        }
    }

    @BindingAdapter("changeSharkViewProgress")
    public static void changeSharkViewProgress(AchievementProgressView view, PendingAchievementItemViewModel viewModel) {
        view.setPercent(Float.valueOf(viewModel.getPendingAchievement().getProgress()) / Float.valueOf(viewModel.getPendingAchievement().getPoints()));
    }

    @BindingAdapter("changeProgressTextFrom")
    public static void changeProgressText(TextView view, PendingAchievementItemViewModel viewModel) {
        view.setText(viewModel.getPendingAchievement().getProgress() + "/" + viewModel.getPendingAchievement().getPoints());
    }

}
