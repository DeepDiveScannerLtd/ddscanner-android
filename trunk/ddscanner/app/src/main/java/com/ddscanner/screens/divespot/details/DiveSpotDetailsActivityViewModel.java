package com.ddscanner.screens.divespot.details;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.rey.material.widget.FloatingActionButton;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class DiveSpotDetailsActivityViewModel {

    private DiveSpotDetailsEntity diveSpotDetailsEntity;
    private ProgressView progressView;

    public DiveSpotDetailsActivityViewModel(DiveSpotDetailsEntity diveSpotDetailsEntity, ProgressView progressView) {
        this.diveSpotDetailsEntity = diveSpotDetailsEntity;
        this.progressView = progressView;
    }

    public ProgressView getProgressView() {
        return progressView;
    }

    public DiveSpotDetailsEntity getDiveSpotDetailsEntity() {
        return diveSpotDetailsEntity;
    }

    @BindingAdapter("loadMainImageFrom")
    public static void loadMainImage(ImageView view, final DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel.getDiveSpotDetailsEntity().getPhotos() != null) {
                Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url,viewModel.getDiveSpotDetailsEntity().getCoverPhotoId(), "2")).into(view, new ImageLoadedCallback(viewModel.getProgressView()));
                return;
            }
            viewModel.getProgressView().setVisibility(View.GONE);
            view.setImageResource(R.drawable.ds_head_photo_default);
        }
    }

    @BindingAdapter("changeVisibilityAccording")
    public static void changeViewSate(RelativeLayout view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() != SharedPreferenceHelper.UserType.DIVECENTER) {
                view.setVisibility(View.GONE);
            } else {
                if (!viewModel.getDiveSpotDetailsEntity().getFlags().isApproved()) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    @BindingAdapter("changeCheckinButtonVisibilityFrom")
    public static void changeVisibilityCheckinButton(FloatingActionButton button, DiveSpotDetailsActivityViewModel viewModel) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() != SharedPreferenceHelper.UserType.DIVECENTER) {
            button.setVisibility(View.VISIBLE);
        }
    }

    @BindingAdapter("showWorkingLayoutFrom")
    public static void showLayout(RelativeLayout view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    @BindingAdapter("loadRatingFrom")
    public static void setDiveSpotRating(LinearLayout view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            for (int k = 0; k < Math.round(viewModel.getDiveSpotDetailsEntity().getRating()); k++) {
                ImageView iv = new ImageView(view.getContext());
                iv.setImageResource(R.drawable.ic_ds_star_full);
                iv.setPadding(0, 0, 5, 0);
                view.addView(iv);
            }
            for (int k = 0; k < 5 - Math.round(viewModel.getDiveSpotDetailsEntity().getRating()); k++) {
                ImageView iv = new ImageView(view.getContext());
                iv.setImageResource(R.drawable.ic_ds_star_empty);
                iv.setPadding(0, 0, 5, 0);
                view.addView(iv);
            }
        }
    }

    @BindingAdapter("loadCheckinsFrom")
    public static void setCheckinsCount(TextView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            switch (viewModel.getDiveSpotDetailsEntity().getCheckinCount()) {
                case 0:
                    view.setText(DDScannerApplication.getInstance().getString(R.string.no_one_has_checked_in_here));
                    break;
                case 1:
                    view.setText("1 " + DDScannerApplication.getInstance().getString(R.string.one_person_checked_in));
                    break;
                default:
                    view.setText(String.valueOf(viewModel.getDiveSpotDetailsEntity().getCheckinCount()) + " " + DDScannerApplication.getInstance().getString(R.string.peoples_checked_in_here));
                    break;
            }
        }
    }

    @BindingAdapter("loadCreatorAvatarFrom")
    public static void loadCreatorAvatar(ImageView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.server_api_address) + Constants.USER_IMAGE_PATH_PREVIEW + viewModel.getDiveSpotDetailsEntity().getAuthor().getPhoto())
                    .resize(Math.round(Helpers.convertDpToPixel(20, view.getContext())), Math.round(Helpers.convertDpToPixel(20, view.getContext())))
                    .centerCrop()
                    .placeholder(R.drawable.gray_circle_placeholder)
                    .error(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(view);
        }
    }

    @BindingAdapter("changeVisibilityFrom")
    public static void setVisibilityText(TextView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setText(DDScannerApplication.getInstance().getString(R.string.visibility_pattern, viewModel.getDiveSpotDetailsEntity().getVisibilityMin(), viewModel.getDiveSpotDetailsEntity().getVisibilityMax()));
        }
    }

    @BindingAdapter("setCountReviewsFrom")
    public static void setReviewsCount(Button view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (viewModel.getDiveSpotDetailsEntity().getReviewsCount() == 0) {
                view.setText(DDScannerApplication.getInstance().getString(R.string.write_review));
                return;
            }
            view.setText(DDScannerApplication.getInstance().getString(R.string.show_all, String.valueOf(viewModel.getDiveSpotDetailsEntity().getReviewsCount())));
        }
    }

    @BindingAdapter("visibilityForBookButtonFrom")
    public static void setBookButtonVisibility(Button view, DiveSpotDetailsActivityViewModel viewModel) {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("reviewsRatingLayoutVisibilityFrom")
    public static void setVisibilityReviewsBlock(LinearLayout view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn() && DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER && viewModel.getDiveSpotDetailsEntity().getReviewsCount() == 0) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    @BindingAdapter("ratingLayoutVisibilityFrom")
    public static void setVisibilityOfRatingLayout(LinearLayout layout, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
                    layout.setVisibility(View.GONE);
                } else if (viewModel.getDiveSpotDetailsEntity().getFlags().isReviewed()) {
                    layout.setVisibility(View.GONE);
                }
            } else {
                layout.setVisibility(View.VISIBLE);
            }
        }
    }

    private static class ImageLoadedCallback implements Callback {
        ProgressView progressBar;

        public ImageLoadedCallback(ProgressView progBar) {
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onError() {

        }
    }

}
