package com.ddscanner.ui.fragments;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.events.ChooseProfileFragmentViewEvent;
import com.ddscanner.events.GetPhotoFromCameraEvent;
import com.ddscanner.events.TakePhotoFromCameraEvent;
import com.ddscanner.ui.views.TransformationRoundImage;
import com.ddscanner.utils.Helpers;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

/**
 * Created by lashket on 20.4.16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private LinearLayout editProfile;
    private ScrollView aboutLayout;
    private LinearLayout editLayout;
    private ImageView capturePhoto;
    private ImageView newPhoto;
    private Helpers helpers = new Helpers();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        aboutLayout = (ScrollView) v.findViewById(R.id.about);
        editLayout = (LinearLayout) v.findViewById(R.id.editProfile);
        editProfile = (LinearLayout) v.findViewById(R.id.edit_profile);
        capturePhoto = (ImageView) v.findViewById(R.id.capture_photo);
        newPhoto = (ImageView) v.findViewById(R.id.user_chosed_photo);

        editProfile.setOnClickListener(this);
        capturePhoto.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                aboutLayout.setVisibility(View.GONE);
                editLayout.setVisibility(View.VISIBLE);
            //    DDScannerApplication.bus.post(new ChooseProfileFragmentViewEvent(true));
                break;
            case R.id.capture_photo:
                DDScannerApplication.bus.post(new TakePhotoFromCameraEvent());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    public void setImage(Uri uri) {
        Picasso.with(getContext()).load(uri)
                .transform(new TransformationRoundImage(50,0)).into(newPhoto);
    }

}
