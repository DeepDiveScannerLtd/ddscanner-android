package com.ddscanner.screens.user.likes;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class LikeReviewViewModel {

    private LikeEntity likeEntity;

    public LikeReviewViewModel(LikeEntity likeEntity) {
        this.likeEntity = likeEntity;
    }

    public LikeEntity getLikeEntity() {
        return likeEntity;
    }

    @BindingAdapter("loadAvatarFrom")
    public static void loadUseravatar(ImageView view, LikeReviewViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext())
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getLikeEntity().getUser().getPhoto(), "1"))
                    .placeholder(R.drawable.avatar_profile_default)
                    .error(R.drawable.avatar_profile_default)
                    .resize(Math.round(Helpers.convertDpToPixel(40, view.getContext())), Math.round(Helpers.convertDpToPixel(40, view.getContext())))
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .into(view);
        }
    }

    @BindingAdapter("setTextFrom")
    public static void setMainText(TextView view, LikeReviewViewModel viewModel) {
        if (viewModel != null) {
            String text = viewModel.getLikeEntity().getReview().getReview();
            if (text.length() > 30) {
                text = reformatString(text);
            }
            view.setText(DDScannerApplication.getInstance().getString(R.string.foreign_user_likes_main_text, viewModel.getLikeEntity().getDiveSpot().getName(), text));
        }
    }

    @BindingAdapter("loadTimeFrom")
    public static void setTimeAgoText(TextView view, LikeReviewViewModel viewModel) {
        if (viewModel != null) {
            view.setText(Helpers.getDate(viewModel.getLikeEntity().getDate()));
        }
    }

    @BindingAdapter("likedTextFrom")
    public static void setLikedText(TextView view, LikeReviewViewModel viewModel) {
        if (viewModel != null) {
            view.setText(DDScannerApplication.getInstance().getString(R.string.foreign_user_like_by, viewModel.getLikeEntity().getUser().getName()));
        }
    }

    public static String reformatString(String firstString) {
        firstString = firstString.substring(0, 27);
        firstString = firstString + "...";
        return firstString;
    }

}
