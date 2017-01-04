package com.ddscanner.screens.profile.edit;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.screens.profile.DiveCenterProfileFragmentViewModel;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class EditDiveCenterProfileActivityViewModel {

    private DiveCenterProfile diveCenterProfile;

    public EditDiveCenterProfileActivityViewModel(DiveCenterProfile diveCenterProfile) {
        this.diveCenterProfile = diveCenterProfile;
    }

    public DiveCenterProfile getDiveCenterProfile() {
        return diveCenterProfile;
    }

    @BindingAdapter({"loadImageFrom"})
    public static void loadProfileImage(ImageView view, EditDiveCenterProfileActivityViewModel viewModel) {
        if (viewModel != null && viewModel.getDiveCenterProfile().getPhoto() != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getDiveCenterProfile().getPhoto(), "1")).resize(Math.round(Helpers.convertDpToPixel(65, view.getContext())), Math.round(Helpers.convertDpToPixel(65, view.getContext()))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.ALL)).into(view);
        }
    }



}
