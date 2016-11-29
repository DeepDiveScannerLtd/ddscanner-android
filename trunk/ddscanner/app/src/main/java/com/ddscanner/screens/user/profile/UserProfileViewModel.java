package com.ddscanner.screens.user.profile;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.entities.User;

public class UserProfileViewModel {

    private User user;

    public UserProfileViewModel(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @BindingAdapter("loadImageFrom")
    public static void loadImage(ImageView view, UserProfileViewModel viewModel) {

    }


}
