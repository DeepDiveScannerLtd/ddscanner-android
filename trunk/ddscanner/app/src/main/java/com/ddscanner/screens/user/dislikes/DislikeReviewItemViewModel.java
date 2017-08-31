package com.ddscanner.screens.user.dislikes;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.events.OpenReviewActivityEvent;
import com.ddscanner.screens.user.likes.LikeReviewViewModel;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.LinkConsumableTextView;

import java.util.ArrayList;

public class DislikeReviewItemViewModel extends LikeReviewViewModel{

    public DislikeReviewItemViewModel(LikeEntity likeEntity) {
        super(likeEntity);
    }

    @BindingAdapter("setDislikedTextFrom")
    public static void setMainText(LinkConsumableTextView view, LikeReviewViewModel viewModel) {
        if (viewModel != null) {
            ArrayList<Link> links = new ArrayList<>();
            String text = viewModel.getLikeEntity().getReview().getReview();
            if (text != null) {
                if (text.length() > 30) {
                    text = reformatString(text);
                }
            }
            view.setText(DDScannerApplication.getInstance().getString(R.string.foreign_user_dislikes_main_text, viewModel.getLikeEntity().getUser().getName(), text));
            links.add(getLinkForText(viewModel.getLikeEntity().getUser().getName(), clickedText -> UserProfileActivity.show(view.getContext(), viewModel.getLikeEntity().getUser().getId(), viewModel.getLikeEntity().getUser().getType())));
            links.add(getLinkForText(text, clickedText -> DDScannerApplication.bus.post(new OpenReviewActivityEvent(viewModel.getLikeEntity().getReview().getId()))));
            LinkBuilder.on(view).addLinks(links).build();
        }
    }
    
}
