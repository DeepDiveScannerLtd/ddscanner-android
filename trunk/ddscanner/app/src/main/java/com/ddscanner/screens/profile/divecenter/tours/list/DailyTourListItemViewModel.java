package com.ddscanner.screens.profile.divecenter.tours.list;


import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DailyTour;
import com.squareup.picasso.Picasso;

public class DailyTourListItemViewModel {

    private DailyTour dailyTour;

    public DailyTourListItemViewModel(DailyTour dailyTour) {
        this.dailyTour = dailyTour;
    }

    public DailyTour getDailyTour() {
        return dailyTour;
    }

    @BindingAdapter("loadPhotoFrom")
    public static void loadTourPhoto(ImageView view, String photo) {
        if (photo != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photo, "2")).placeholder(R.drawable.gray_rectangle_without_corners).into(view);
        }
    }

    @BindingAdapter("loadDivesCountFrom")
    public static void loadDivesCount(TextView view, String divesCount) {
        if (divesCount != null) {
            view.setText(String.format("%s%s", divesCount, DDScannerApplication.getInstance().getString(R.string.dives_pattern)));
        } else {
            view.setText(DDScannerApplication.getInstance().getString(R.string.empty_string));
        }
    }

}
