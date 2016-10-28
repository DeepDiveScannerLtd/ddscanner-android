package com.ddscanner.screens.sealife.details;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ddscanner.entities.Sealife;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class SealifeViewModel {
    private Sealife sealife;
    private String imagePath;
    private float imageWidth;
    private ProgressBar progressBar;

    public SealifeViewModel(Sealife sealife, String imagePath, float imageWidth, ProgressBar progressBar) {
        this.sealife = sealife;
        this.imagePath = imagePath;
        this.imageWidth = imageWidth;
        this.progressBar = progressBar;
    }

    public Sealife getSealife() {
        return sealife;
    }

    public String getImagePath() {
        return imagePath;
    }

    public float getImageWidth() {
        return imageWidth;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @BindingAdapter({"sealifeViewModel"})
    public static void loadImage(ImageView view, SealifeViewModel sealifeViewModel) {
        Picasso.with(view.getContext())
                .load(sealifeViewModel.getImagePath() + sealifeViewModel.getSealife().getImage())
                .resize(Math.round(sealifeViewModel.getImageWidth()), 239)
                .centerCrop()
                .into(view, new ImageLoadedCallback(sealifeViewModel.getProgressBar()));
    }

    private static class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar progBar) {
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