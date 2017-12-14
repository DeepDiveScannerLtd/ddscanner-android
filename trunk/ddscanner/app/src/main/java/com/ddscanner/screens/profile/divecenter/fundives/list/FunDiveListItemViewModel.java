package com.ddscanner.screens.profile.divecenter.fundives.list;


import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.FunDive;
import com.squareup.picasso.Picasso;

public class FunDiveListItemViewModel {

    private FunDive funDive;

    public FunDiveListItemViewModel(FunDive funDive) {
        this.funDive = funDive;
    }

    public FunDive getFunDive() {
        return funDive;
    }

    @BindingAdapter("loadPhotoFrom")
    public static void loadFunDivePhotoFrom(ImageView view, String photo) {
        if (photo != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, photo, "2")).placeholder(R.drawable.gray_rectangle_without_corners).into(view);
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.produc_def));
        }
    }

}
