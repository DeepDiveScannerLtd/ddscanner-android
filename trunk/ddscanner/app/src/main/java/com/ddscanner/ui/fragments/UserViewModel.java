package com.ddscanner.ui.fragments;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class UserViewModel {

    private User user;

    public UserViewModel(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @BindingAdapter({"countFavoriteFrom"})
    public static void favoritesCount (TextView view, UserViewModel userViewModel) {
        if (userViewModel != null) {
            view.setText(getDiveSpotString(userViewModel.getUser().getCounters().getFavoritesCount()));
        }
    }

    @BindingAdapter({"countCheckinsFrom"})
    public static void checkinsCount (TextView view, UserViewModel userViewModel) {
        if (userViewModel != null) {
            view.setText(getDiveSpotString(userViewModel.getUser().getCounters().getCheckinsCount()));
        }
    }

    @BindingAdapter({"countEditedFrom"})
    public static void editedCount (TextView view, UserViewModel userViewModel) {
        if (userViewModel != null) {
            view.setText(getDiveSpotString(userViewModel.getUser().getCounters().getEditedCount()));
        }
    }

    @BindingAdapter({"countAddedFrom"})
    public static void addedCount (TextView view, UserViewModel userViewModel) {
        if (userViewModel != null) {
            view.setText(getDiveSpotString(userViewModel.getUser().getCounters().getAddedCount()));
        }
    }

    @BindingAdapter({"loadImageFrom"})
    public static void loadImage(ImageView view, UserViewModel userViewModel) {
        if (userViewModel != null && userViewModel.getUser().getPhoto() != null) {
            Picasso.with(view.getContext()).load(userViewModel.getUser().getPhoto())
                    .resize(Math.round(Helpers.convertDpToPixel(100, view.getContext())),
                            Math.round(Helpers.convertDpToPixel(100, view.getContext()))).centerCrop()
                    .placeholder(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(view);
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
