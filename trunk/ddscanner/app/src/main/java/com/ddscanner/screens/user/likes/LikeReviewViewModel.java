package com.ddscanner.screens.user.likes;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.events.OpenReviewActivityEvent;
import com.ddscanner.screens.user.profile.UserProfileActivity;
import com.ddscanner.utils.Helpers;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.LinkConsumableTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
                    .placeholder(R.drawable.gray_circle_placeholder)
                    .error(R.drawable.avatar_profile_default)
                    .resize(Math.round(Helpers.convertDpToPixel(40, view.getContext())), Math.round(Helpers.convertDpToPixel(40, view.getContext())))
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .into(view);
        }
    }

    @BindingAdapter("setTextFrom")
    public static void setMainText(LinkConsumableTextView view, LikeReviewViewModel viewModel) {
        if (viewModel != null) {
            ArrayList<Link> links = new ArrayList<>();
            String text = viewModel.getLikeEntity().getReview().getReview();
            if (text != null) {
                if (text.length() > 30) {
                    text = reformatString(text);
                }
            }
            view.setText(DDScannerApplication.getInstance().getString(R.string.foreign_user_likes_main_text, viewModel.getLikeEntity().getUser().getName(), text));
            links.add(getLinkForText(viewModel.getLikeEntity().getUser().getName(), clickedText -> UserProfileActivity.show(view.getContext(), viewModel.getLikeEntity().getUser().getId(), viewModel.getLikeEntity().getUser().getType())));
            links.add(getLinkForText(text, clickedText -> DDScannerApplication.bus.post(new OpenReviewActivityEvent(viewModel.getLikeEntity().getReview().getId()))));
            LinkBuilder.on(view).addLinks(links).build();
        }
    }

    @BindingAdapter("loadTimeFrom")
    public static void setTimeAgoText(TextView view, LikeReviewViewModel viewModel) {
        if (viewModel != null) {
            view.setText(Helpers.getDate(viewModel.getLikeEntity().getDate()));
        }
    }

    public static String reformatString(String firstString) {
        firstString = firstString.substring(0, 27);
        firstString = firstString + "...";
        return firstString;
    }

    public static Link getLinkForText(String text, Link.OnClickListener clickListener) {
        Link link = new Link(text);
        link.setUnderlined(false);
        link.setTextColor(ContextCompat.getColor(DDScannerApplication.getInstance(),R.color.notification_clickable_text_color));
        link.setHighlightAlpha(0);
        link.setOnClickListener(clickListener);
        return link;
    }

}
