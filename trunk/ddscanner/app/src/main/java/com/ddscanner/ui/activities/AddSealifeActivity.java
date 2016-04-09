package com.ddscanner.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ddscanner.R;
import com.ddscanner.utils.Helpers;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by lashket on 8.4.16.
 */
public class AddSealifeActivity extends AppCompatActivity implements View.OnClickListener{



    private static final int RC_PICK_PHOTO = 1001;
    private CardView addPhoto;
    private RelativeLayout centerLayout;
    private AppCompatImageButton btnDelete;
    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sealife);
        findViews();
    }

    private void findViews() {
        btnDelete = (AppCompatImageButton) findViewById(R.id.delete_photo);
        centerLayout = (RelativeLayout) findViewById(R.id.add_photo_center_layout);
        addPhoto = (CardView) findViewById(R.id.add_photo_layout);

        addPhoto.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_PHOTO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            setBackImage(helpers.getRealPathFromURI(this, uri));
        }
    }

    private void setBackImage(String path) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        path = "file://" + path;
        Picasso.with(this).load(path).resize(Math.round(dpWidth), 230).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                centerLayout.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
                addPhoto.setBackground(ob);
                addPhoto.setOnClickListener(null);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo_layout:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(i, RC_PICK_PHOTO);
                break;
            case R.id.delete_photo:
                addPhoto.setBackground(null);
                addPhoto.setBackgroundColor(getResources().getColor(R.color.white));
                btnDelete.setVisibility(View.GONE);
                centerLayout.setVisibility(View.VISIBLE);
                addPhoto.setOnClickListener(this);
                break;
        }
    }
}
