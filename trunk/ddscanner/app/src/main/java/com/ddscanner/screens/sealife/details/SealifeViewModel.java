package com.ddscanner.screens.sealife.details;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Sealife;
import com.squareup.picasso.Picasso;


public class SealifeViewModel {
    private Sealife sealife;
    private float imageWidth;
    private ProgressBar progressBar;

    public SealifeViewModel(Sealife sealife, float imageWidth, ProgressBar progressBar) {
        this.sealife = sealife;
        this.imageWidth = imageWidth;
        this.progressBar = progressBar;
    }

    public Sealife getSealife() {
        return sealife;
    }

    public float getImageWidth() {
        return imageWidth;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @BindingAdapter({"loadImageFrom"})
    public static void loadImage(ImageView view, SealifeViewModel sealifeViewModel) {
        if (sealifeViewModel != null) {
            Picasso.with(view.getContext())
                    .load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, sealifeViewModel.getSealife().getImage(), "2"))
                    .resize(Math.round(sealifeViewModel.getImageWidth()), 239)
                    .centerCrop()
                    .into(view);
        }
    }

}