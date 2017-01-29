package com.ddscanner.screens.photo.slider;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ddscanner.R;
import com.ddscanner.ui.views.ScallingImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by lashket on 4.3.16.
 */
public class SliderImagesFragment extends Fragment {

    private static final String TAG = SliderImagesFragment.class.getName();
    public static final String IMAGE_URL = "IMAGE_URL";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String imageUrl = getArguments().getString(IMAGE_URL);
        View view = inflater.inflate(R.layout.slider_image_fragment, container, false);

        ScallingImageView imageView = (ScallingImageView) view.findViewById(R.id.slider_image);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        Picasso.with(getActivity()).load(imageUrl).into(imageView);

        return view;
    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public ImageLoadedCallback(ProgressBar progBar) {
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {
            if (this.progressBar != null) {
                this.progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onError() {
            Log.i(TAG, "image loading failed");
        }
    }

}
