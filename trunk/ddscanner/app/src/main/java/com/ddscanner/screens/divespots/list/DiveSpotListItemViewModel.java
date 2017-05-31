package com.ddscanner.screens.divespots.list;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.ui.views.RatingView;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DiveSpotListItemViewModel {

    private DiveSpotShort diveSpot;

    public DiveSpotListItemViewModel(DiveSpotShort diveSpot) {

        this.diveSpot = diveSpot;
    }

    public DiveSpotShort getDiveSpot() {
        return diveSpot;
    }
    
    @BindingAdapter({"loadDiveSpotPhotoFrom"})
    public static void loadPhoto(ImageView view, DiveSpotListItemViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getDiveSpot().getImage(), "1")).resize(Math.round(Helpers.convertDpToPixel(55, view.getContext())), Math.round(Helpers.convertDpToPixel(55, view.getContext()))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.ALL)).placeholder(R.drawable.placeholder_photo_wit_round_corners).error(R.drawable.ds_list_photo_default).into(view);
        }
    }

    @BindingAdapter({"setRatingFrom"})
    public static void setRating(RatingView view, DiveSpotListItemViewModel viewModel) {
        if (viewModel != null) {
            view.removeAllViews();
            view.setRating(Math.round(viewModel.getDiveSpot().getRating()), R.drawable.ic_list_star_full, R.drawable.ic_list_star_empty);
        }
    }

}
