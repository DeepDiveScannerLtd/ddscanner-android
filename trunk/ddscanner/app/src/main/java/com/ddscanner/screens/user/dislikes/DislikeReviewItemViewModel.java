package com.ddscanner.screens.user.dislikes;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.screens.user.likes.LikeReviewViewModel;

public class DislikeReviewItemViewModel extends LikeReviewViewModel{

    public DislikeReviewItemViewModel(LikeEntity likeEntity) {
        super(likeEntity);
    }

    @BindingAdapter("dislikedByTextFrom")
    public static void loadDislikeText(TextView view, DislikeReviewItemViewModel viewModel) {
        if (viewModel != null) {
            view.setText(DDScannerApplication.getInstance().getString(R.string.foreign_user_dislike_by, viewModel.getLikeEntity().getUser().getName()));
        }
    }

}
