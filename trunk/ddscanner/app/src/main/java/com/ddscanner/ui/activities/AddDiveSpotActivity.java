package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.ddscanner.R;

/**
 * Created by lashket on 5.4.16.
 */
public class AddDiveSpotActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_PICK_PHOTO = 9001;

    private ImageButton btnAddPhoto;
    private Toolbar toolbar;


    public static void show(Context context) {
        Intent intent = new Intent(context, AddDiveSpotActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dive_spot);
        findViews();
        toolbarSettings();
    }

    private void findViews() {
        btnAddPhoto = (ImageButton) findViewById(R.id.btn_add_photo);
        btnAddPhoto.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Divespot");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RC_PICK_PHOTO);
                break;
        }
    }
}
