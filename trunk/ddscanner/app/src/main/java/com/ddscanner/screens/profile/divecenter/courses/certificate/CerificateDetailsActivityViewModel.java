package com.ddscanner.screens.profile.divecenter.courses.certificate;


import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.ddscanner.entities.Certificate;

public class CerificateDetailsActivityViewModel {

    private Certificate certificate;

    public CerificateDetailsActivityViewModel(Certificate certificate) {
        this.certificate = certificate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    @BindingAdapter("{loadAssociationLogo}")
    public static void loadAssociationPhoto(ImageView view, Integer resourceId) {
        if (resourceId != null) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), resourceId));
        }
    }
}
