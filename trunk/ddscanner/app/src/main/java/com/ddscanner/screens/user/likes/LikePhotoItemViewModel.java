package com.ddscanner.screens.user.likes;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.utils.Helpers;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.LinkConsumableTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class LikePhotoItemViewModel extends LikeReviewViewModel {

    public LikePhotoItemViewModel(LikeEntity likeEntity) {
        super(likeEntity);
    }

    @BindingAdapter("loadPhotoFrom")
    public static void loadDiveSpotPhoto(ImageView view, LikePhotoItemViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext())
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getLikeEntity().getPhoto().get(0).getId(), "1"))
                    .resize(Math.round(Helpers.convertDpToPixel(40, view.getContext())), Math.round(Helpers.convertDpToPixel(40, view.getContext())))
                    .placeholder(R.drawable.placeholder_photo_wit_round_corners)
                    .error(R.drawable.ds_list_photo_default)
                    .centerCrop()
                    .transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2,view.getContext())), 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(view);
        }
    }

    @BindingAdapter("setLikedPhotoTextFrom")
    public static void setLikedPhotoText(LinkConsumableTextView view, LikePhotoItemViewModel viewModel) {
        if (viewModel != null) {
            ArrayList<Link> links = new ArrayList<>();
            view.setText(DDScannerApplication.getInstance().getString(R.string.user_liked_your_photo, viewModel.getLikeEntity().getUser().getName(), viewModel.getLikeEntity().getDiveSpot().getName()));
            links.add(getLinkForText(viewModel.getLikeEntity().getUser().getName(), clickedText -> UserProfileActivity.show(view.getContext(), viewModel.getLikeEntity().getUser().getId(), viewModel.getLikeEntity().getUser().getType())));
            links.add(getLinkForText(viewModel.getLikeEntity().getDiveSpot().getName(), clickedText -> DiveSpotDetailsActivity.show(view.getContext(), String.valueOf(viewModel.getLikeEntity().getDiveSpot().getId()), EventsTracker.SpotViewSource.UNKNOWN)));
            LinkBuilder.on(view).addLinks(links).build();
        }
    }



}
