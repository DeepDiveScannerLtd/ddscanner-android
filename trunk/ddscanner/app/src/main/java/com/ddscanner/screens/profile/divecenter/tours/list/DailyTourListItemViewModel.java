package com.ddscanner.screens.profile.divecenter.tours.list;


import android.databinding.BindingAdapter;
import android.widget.ImageView;

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
            Picasso.with(view.getContext()).load(photo).into(view);
        }
    }

}
