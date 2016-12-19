package com.ddscanner.screens.profile;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenterProfile;

public class DiveCenterProfileFragmentViewModel {

    private DiveCenterProfile diveCenterProfile;

    public DiveCenterProfileFragmentViewModel(DiveCenterProfile diveCenterProfile) {
        this.diveCenterProfile = diveCenterProfile;
    }

    public DiveCenterProfile getDiveCenterProfile() {
        return diveCenterProfile;
    }

    @BindingAdapter({"countEditedFrom"})
    public static void editedCount(TextView view, DiveCenterProfileFragmentViewModel diveCenterProfileFragmentViewModel) {
        if (diveCenterProfileFragmentViewModel != null) {
            view.setText(getDiveSpotString(diveCenterProfileFragmentViewModel.getDiveCenterProfile().getEditedSpotsCount()));
        }
    }

    @BindingAdapter({"countAddedFrom"})
    public static void addedCount(TextView view, DiveCenterProfileFragmentViewModel diveCenterProfileFragmentViewModel) {
        if (diveCenterProfileFragmentViewModel != null) {
            view.setText(getDiveSpotString(diveCenterProfileFragmentViewModel.getDiveCenterProfile().getCreatedSpotsCount()));
        }
    }

    @BindingAdapter({"myDiveSpotsCount"})
    public static void mySpotsCount(Button view, DiveCenterProfileFragmentViewModel diveCenterProfileFragmentViewModel) {
        if (diveCenterProfileFragmentViewModel !=null) {
            if (diveCenterProfileFragmentViewModel.getDiveCenterProfile().getWorkingCount() > 0) {
                view.setVisibility(View.VISIBLE);
                view.setText(DDScannerApplication.getInstance().getString(R.string.dive_center_spots_count, String.valueOf(diveCenterProfileFragmentViewModel.getDiveCenterProfile().getWorkingCount())));
            }
        }
    }

    private static String getDiveSpotString(int count) {
        if (count > 1 || count == 0) {
            return String.valueOf(count) + DDScannerApplication.getInstance().getString(R.string.dive_spos);
        }
        if (count == 1) {
            return String.valueOf(count) + DDScannerApplication.getInstance().getString(R.string.one_dive_spot);
        }
        return "";
    }

}
