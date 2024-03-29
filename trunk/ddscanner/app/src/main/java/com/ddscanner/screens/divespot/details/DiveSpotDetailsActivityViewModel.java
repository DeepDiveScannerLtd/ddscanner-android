package com.ddscanner.screens.divespot.details;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.ui.views.DiveSpotCharacteristicView;
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
            view.setImageResource(R.drawable.ds_head_photo_def);
        }
    }

    @BindingAdapter("changeVisibilityAccording")
    public static void changeViewSate(RelativeLayout view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (!SharedPreferenceHelper.getIsUserSignedIn() || (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVER)) {
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
        if (SharedPreferenceHelper.getActiveUserType() != SharedPreferenceHelper.UserType.DIVECENTER) {
            button.setVisibility(View.VISIBLE);
        }
    }

    @BindingAdapter("showWorkingLayoutFrom")
    public static void showLayout(RelativeLayout view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
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
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getDiveSpotDetailsEntity().getAuthor().getPhoto(), "1"))
                    .resize(Math.round(Helpers.convertDpToPixel(20, view.getContext())), Math.round(Helpers.convertDpToPixel(20, view.getContext())))
                    .centerCrop()
                    .placeholder(R.drawable.gray_circle_placeholder)
                    .error(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(view);
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
        if (viewModel != null) {
//            if (SharedPreferenceHelper.getActiveUserType() != SharedPreferenceHelper.UserType.DIVECENTER) {
//                view.setVisibility(View.VISIBLE);
//                return;
//            }
            if (!viewModel.getDiveSpotDetailsEntity().isSomebodyWorkingHere()) {
                view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.text_inactive_button));
            }
        }
    }

    @BindingAdapter("reviewsRatingLayoutVisibilityFrom")
    public static void setVisibilityReviewsBlock(LinearLayout view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (SharedPreferenceHelper.getIsUserSignedIn() && SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER && viewModel.getDiveSpotDetailsEntity().getReviewsCount() == 0) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    @BindingAdapter("ratingLayoutVisibilityFrom")
    public static void setVisibilityOfRatingLayout(LinearLayout layout, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            if (SharedPreferenceHelper.getIsUserSignedIn()) {
                if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
                    layout.setVisibility(View.GONE);
                } else if (viewModel.getDiveSpotDetailsEntity().getFlags().isReviewed()) {
                    layout.setVisibility(View.GONE);
                }
            } else {
                layout.setVisibility(View.VISIBLE);
            }
        }
    }

    @BindingAdapter("setCountryFrom")
    public static void setCountryFrom(DiveSpotCharacteristicView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setViewData(R.drawable.ic_ds_country, R.string.country_characteristic, viewModel.getDiveSpotDetailsEntity().getCountry().getName());
        }
    }

    @BindingAdapter("setObjectFrom")
    public static void setObjectFrom(DiveSpotCharacteristicView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setViewData(R.drawable.ic_ds_object, R.string.object_characteristic, viewModel.getDiveSpotDetailsEntity().getObject());
        }
    }

    @BindingAdapter("setLevelFrom")
    public static void setLevelFrom(DiveSpotCharacteristicView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setViewData(R.drawable.ic_ds_level, R.string.details_level, viewModel.getDiveSpotDetailsEntity().getDiverLevel());
        }
    }

    @BindingAdapter("setDepthFrom")
    public static void setDepthFrom(DiveSpotCharacteristicView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setViewData(R.drawable.ic_ds_deep_2, R.string.details_depth, viewModel.getDiveSpotDetailsEntity().getDepth());
        }
    }

    @BindingAdapter("setVisibilityFrom")
    public static void setVisibilityFrom(DiveSpotCharacteristicView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            String visibility = DDScannerApplication.getInstance().getString(R.string.visibility_pattern, viewModel.getDiveSpotDetailsEntity().getVisibilityMin(), viewModel.getDiveSpotDetailsEntity().getVisibilityMax());
            view.setViewData(R.drawable.ic_ds_visibility, R.string.details_visibility, visibility);
        }
    }

    @BindingAdapter("setCurrentFrom")
    public static void setCurrentFrom(DiveSpotCharacteristicView view, DiveSpotDetailsActivityViewModel viewModel) {
        if (viewModel != null) {
            view.setViewData(R.drawable.ic_ds_currents, R.string.details_currents, viewModel.getDiveSpotDetailsEntity().getCurrents());
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
