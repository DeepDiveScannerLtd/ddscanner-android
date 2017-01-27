package com.ddscanner.screens.profile.divecenter;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Address;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

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

    @BindingAdapter({"loadImageFrom"})
    public static void loadProfileImage(ImageView view, DiveCenterProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getDiveCenterProfile().getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(65, view.getContext())), Math.round(Helpers.convertDpToPixel(65, view.getContext()))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.ALL)).into(view);
        }
    }

    @BindingAdapter({"setAddressFrom"})
    public static void setAddresses(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        String outString = "";
        if (viewModel != null && viewModel.getDiveCenterProfile().getAddresses() != null) {
            for (Address address : viewModel.getDiveCenterProfile().getAddresses()) {
                outString = outString + address.getName() + " ";
            }
        }
        view.setText(outString);
    }

    @BindingAdapter({"setEmails"})
    public static void setEmailsFrom(TextView view, DiveCenterProfileFragmentViewModel viewModel) {
        String outString = "";
        if (viewModel != null && viewModel.getDiveCenterProfile().getEmails() != null) {
            for (String email : viewModel.getDiveCenterProfile().getEmails()) {
                outString = outString + email + " ";
            }
        }
        view.setText(outString);
    }

    @BindingAdapter({"setPhones"})
    public static void setPhones(TextView view, DiveCenterProfileFragmentViewModel viewModel){
        String outString = "";
        if (viewModel != null && viewModel.getDiveCenterProfile().getPhones() != null) {
            for (String phone : viewModel.getDiveCenterProfile().getPhones()) {
                outString = outString + phone + " ";
            }
        }
        view.setText(outString);
    }

    @BindingAdapter("arrowVisibilityFrom")
    public static void setVisibilityForInstructorsArrow(ImageView view, DiveCenterProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel.getDiveCenterProfile().getInstructorsCount() != null && Integer.parseInt(viewModel.getDiveCenterProfile().getInstructorsCount()) > 0) {
                view.setVisibility(View.VISIBLE);
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
