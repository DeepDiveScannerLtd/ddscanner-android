package com.ddscanner.screens.reiews.list;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.ui.views.RatingView;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ReviewItemViewModel {

    private CommentEntity commentEntity;

    public ReviewItemViewModel(CommentEntity commentEntity) {
        this.commentEntity = commentEntity;
    }

    public CommentEntity getCommentEntity() {
        return commentEntity;
    }

    @BindingAdapter({"loadProfileImageFrom"})
    public static void loadProfileImage(ImageView view, ReviewItemViewModel viewModel) {
        if (viewModel != null) {
            switch (viewModel.getCommentEntity().getReviewType()) {
                case USER:
                    Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getCommentEntity().getDiveSpot().getImage(), "1")).resize(Math.round(Helpers.convertDpToPixel(40, view.getContext())), Math.round(Helpers.convertDpToPixel(40, view.getContext()))).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.ALL)).centerCrop().placeholder(R.drawable.placeholder_photo_wit_round_corners).error(R.drawable.placeholder_photo_wit_round_corners).into(view);
                    break;
                case DIVESPOT:
                    Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getCommentEntity().getAuthor().getPhoto(), "1")).placeholder(R.drawable.review_item_profile_photo_paceholder).resize(Math.round(Helpers.convertDpToPixel(40, view.getContext())), Math.round(Helpers.convertDpToPixel(40, view.getContext()))).centerCrop().transform(new CropCircleTransformation()).into(view);
                    break;
            }
        }
    }

    @BindingAdapter({"loadNameFrom"})
    public static void loadName(TextView view, ReviewItemViewModel viewModel) {
        if (viewModel != null) {
            switch (viewModel.getCommentEntity().getReviewType()) {
                case USER:
                    view.setText(viewModel.getCommentEntity().getDiveSpot().getName());
                    break;
                case DIVESPOT:
                    view.setText(viewModel.getCommentEntity().getAuthor().getName());
                    break;
            }
        }
    }

    @BindingAdapter({"setRatingFrom"})
    public static void setReviewRating(RatingView view, ReviewItemViewModel viewModel) {
        if (viewModel != null) {
            view.setRating(viewModel.getCommentEntity().getComment().getRating(), R.drawable.ic_list_star_full, R.drawable.ic_list_star_empty);
        }
    }

    @BindingAdapter({"setDateFrom"})
    public static void setDate(TextView view, ReviewItemViewModel viewModel) {
        if (viewModel != null) {
            view.setText(Helpers.getCommentDate(viewModel.getCommentEntity().getComment().getDate()));
        }
    }

}
