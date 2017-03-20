package com.ddscanner.screens.booking.offers.dailytours;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.entities.DiveCenterShort;
import com.squareup.picasso.Picasso;

public class DiveCenterListItemViewModel {

    private DiveCenterShort diveCenter;

    public DiveCenterListItemViewModel(DiveCenterShort diveCenter) {
        this.diveCenter = diveCenter;
    }

    public DiveCenterShort getDiveCenter() {
        return diveCenter;
    }

    @BindingAdapter({"loadDiveCenterLogoFrom"})
    public static void loadLogo(ImageView view, DiveCenterListItemViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(viewModel.getDiveCenter().getPhoto()).into(view);
        }
    }

}
