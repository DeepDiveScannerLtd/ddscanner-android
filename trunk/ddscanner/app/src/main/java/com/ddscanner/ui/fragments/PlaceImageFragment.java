package com.ddscanner.ui.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.ui.activities.DivePlaceActivity;
import com.ddscanner.ui.activities.ImageSliderActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by Vitaly on 28.11.2015.
 */
public class PlaceImageFragment extends Fragment {

    public static final String IMAGE_URL = "IMAGE_URL";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String imageUrl = getArguments().getString(IMAGE_URL);

        SimpleDraweeView imageView = (SimpleDraweeView) inflater.inflate(R.layout.fragment_image, container, false);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSliderActivity.show(getActivity(), (ArrayList<String>) DivePlaceActivity.getImages());
            }
        });
        Uri uri = Uri.parse(imageUrl);
        imageView.setImageURI(uri);
        return imageView;
    }

}
