package com.ddscanner.utils;

import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

/**
 * Created by lashket on 6.6.16.
 */
public class ImageLoadedCallback implements Callback {
    ProgressBar progressBar;

    public  ImageLoadedCallback(ProgressBar progBar){
        progressBar = progBar;
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError() {

    }
}
