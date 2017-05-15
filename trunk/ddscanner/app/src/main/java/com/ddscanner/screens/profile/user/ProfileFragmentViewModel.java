package com.ddscanner.screens.profile.user;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ProfileFragmentViewModel {

    private User user;

    public ProfileFragmentViewModel(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @BindingAdapter({"countFavoriteFrom"})
    public static void favoritesCount (TextView view, ProfileFragmentViewModel profileFragmentViewModel) {
        if (profileFragmentViewModel != null) {
            view.setText(getDiveSpotString(profileFragmentViewModel.getUser().getCounters().getFavoritesCount()));
        }
    }

    @BindingAdapter({"countCheckinsFrom"})
    public static void checkinsCount (TextView view, ProfileFragmentViewModel profileFragmentViewModel) {
        if (profileFragmentViewModel != null) {
            view.setText(getDiveSpotString(profileFragmentViewModel.getUser().getCounters().getCheckinsCount()));
        }
    }

    @BindingAdapter({"countEditedFrom"})
    public static void editedCount (TextView view, ProfileFragmentViewModel profileFragmentViewModel) {
        if (profileFragmentViewModel != null) {
            view.setText(getDiveSpotString(profileFragmentViewModel.getUser().getCounters().getEditedCount()));
        }
    }

    @BindingAdapter({"countAddedFrom"})
    public static void addedCount (TextView view, ProfileFragmentViewModel profileFragmentViewModel) {
        if (profileFragmentViewModel != null) {
            view.setText(getDiveSpotString(profileFragmentViewModel.getUser().getCounters().getAddedCount()));
        }
    }

    @BindingAdapter({"diverLevelFrom"})
    public static void diverLevelLabel(TextView view, ProfileFragmentViewModel profileFragmentViewModel) {
        if (profileFragmentViewModel != null && profileFragmentViewModel.getUser().getDiverLevel() != null && profileFragmentViewModel.getUser().getDiverLevel() > 0) {
            view.setText(profileFragmentViewModel.getUser().getDiverLevelString());
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter({"loadImageFrom"})
    public static void loadImage(ImageView view, ProfileFragmentViewModel profileFragmentViewModel) {
        if (profileFragmentViewModel != null && !profileFragmentViewModel.getUser().getPhoto().isEmpty()) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, profileFragmentViewModel.getUser().getPhoto(), "2"))
                    .resize(Math.round(Helpers.convertDpToPixel(50, view.getContext())),
                            Math.round(Helpers.convertDpToPixel(50, view.getContext()))).centerCrop()
                    .placeholder(R.drawable.gray_circle_placeholder)
                    .error(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(view);
        }
    }

    @BindingAdapter({"loadDiveCenterImageFrom"})
    public static void loadDIveCenterImage(ImageView view, ProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel.getUser().getDiveCenter() != null && viewModel.getUser().getDiveCenter().getPhoto() != null) {
                view.setVisibility(View.VISIBLE);
                Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getUser().getDiveCenter().getPhoto(), "1"))
                        .resize(Math.round(Helpers.convertDpToPixel(36, view.getContext())),
                                Math.round(Helpers.convertDpToPixel(36, view.getContext()))).centerCrop()
                        .placeholder(R.drawable.placeholder_photos_activity)
                        .transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, view.getContext())), 0, RoundedCornersTransformation.CornerType.ALL)).into(view);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    @BindingAdapter({"countReviewsFrom"})
    public static void setReviewsCount(TextView view, ProfileFragmentViewModel viewModel) {
        if (viewModel != null) {
            switch (viewModel.getUser().getCounters().getCommentsCount()) {
                case 1:
                    view.setText(DDScannerApplication.getInstance().getString(R.string.single_review_pattern, "1"));
                    break;
                default:
                    view.setText(DDScannerApplication.getInstance().getString(R.string.not_single_review_pattern, String.valueOf(viewModel.getUser().getCounters().getCommentsCount())));
                    break;
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
