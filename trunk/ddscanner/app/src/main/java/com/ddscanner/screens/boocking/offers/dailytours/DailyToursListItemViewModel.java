package com.ddscanner.screens.boocking.offers.dailytours;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.entities.Offer;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DailyToursListItemViewModel {

    private Offer offer;

    public DailyToursListItemViewModel(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    @BindingAdapter({"loadPhotoFrom"})
    public static void loadOfferImage(ImageView view, DailyToursListItemViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(viewModel.getOffer().getImage()).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.TOP)).into(view);
        }
    }

}
