package com.ddscanner.screens.profile.edit;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class EditProfileActivityViewModel {

    private User user;

    public EditProfileActivityViewModel(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @BindingAdapter({"loadImageFrom"})
    public static void loadAvatar(ImageView view, EditProfileActivityViewModel viewModel) {
        if (viewModel != null && viewModel.getUser().getPhoto() != null) {
            Picasso.with(view.getContext()).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, viewModel.getUser().getPhoto(), "2"))
                    .resize(Math.round(Helpers.convertDpToPixel(100, view.getContext())),
                            Math.round(Helpers.convertDpToPixel(100, view.getContext()))).centerCrop()
                    .placeholder(R.drawable.avatar_profile_default)
                    .error(R.drawable.avatar_profile_default)
                    .transform(new CropCircleTransformation()).into(view);
        }
    }
    
}
