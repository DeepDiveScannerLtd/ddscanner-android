package com.ddscanner.screens.photo.crop;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ddscanner.R;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.shape.CropIwaOvalShape;

public class CropImageActivity extends BaseAppCompatActivity {

    public enum CropImageSource {
        SEALIFE, USER_PROFILE, DIVE_CENTER_PROFILE
    }

    CropIwaView cropView;
    CropImageSource source;

    private static final String ARG_IMAGE = "path";
    private static final String ARG_SOURCE = "source";

    public static void showForResult(Activity context, Uri uri, int requestCode, CropImageSource source) {
        Intent intent = new Intent(context, CropImageActivity.class);
        intent.putExtra(ARG_IMAGE, uri);
        intent.putExtra(ARG_SOURCE, source);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        cropView = findViewById(R.id.crop_view);
        Uri uri = getIntent().getParcelableExtra(ARG_IMAGE);
        source = getIntent().getParcelableExtra(ARG_SOURCE);
        cropView.setImageUri(uri);
//        cropView.setImageUri(Uri.parse(getIntent().getStringExtra(ARG_IMAGE)));
//        cropView.crop(new CropIwaSaveConfig.Builder(Uri.parse(getIntent().getStringExtra(ARG_IMAGE))).build());
        cropView.configureOverlay().setDynamicCrop(false).setCropShape(new CropIwaOvalShape(cropView.configureOverlay())).setAspectRatio(new AspectRatio(1,1)).apply();
    }
}
