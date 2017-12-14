package com.ddscanner.screens.profile.divecenter.fundives.details;


import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.FunDiveDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FunDiveDetailsActivityViewModel {

    private FunDiveDetails funDiveDetails;

    public FunDiveDetailsActivityViewModel(FunDiveDetails funDiveDetails) {
        this.funDiveDetails = funDiveDetails;
    }

    public FunDiveDetails getFunDiveDetails() {
        return funDiveDetails;
    }

    @BindingAdapter("loadMainPhotoFrom")
    public static void loadMainPhoto(ImageView view, String photo) {
        if (photo != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photo, "2")).placeholder(R.drawable.gray_rectangle_without_corners).into(view);
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.produc_def));
        }
    }


}
