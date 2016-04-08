package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAdapter;
import com.ddscanner.ui.managers.SealifeLinearLayoutManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 5.4.16.
 */
public class AddDiveSpotActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddDiveSpotActivity.class.getSimpleName();
    private static final int RC_PICK_PHOTO = 9001;
    private static final int RC_PICK_LOCATION = 8001;

    private ImageButton btnAddPhoto;
    private ImageView btnAddSealife;

    private Toolbar toolbar;
    private LatLng diveSpotLocation;

    private RelativeLayout pickLocation;
    private RecyclerView photos_rc;
    private TextView addPhotoTitle;
    private TextView locationTitle;

    private List<String> imageUris = new ArrayList<String>();


    private AppCompatSpinner visibilitySpinner;

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
        setRcSettings();
        setSpinnerData();
    }

    private void setSpinnerData() {
        String[] data = {"one", "two", "three", "four", "five"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        visibilitySpinner.setAdapter(adapter);
    }

    private void setRcSettings() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
    }

    private void findViews() {
        btnAddSealife = (ImageView) findViewById(R.id.btn_add_sealife);
        locationTitle = (TextView) findViewById(R.id.characteristic_location_title);
        visibilitySpinner = (AppCompatSpinner) findViewById(R.id.characteristic_visibility_spinner);
        addPhotoTitle = (TextView) findViewById(R.id.add_photo_title);
        photos_rc = (RecyclerView) findViewById(R.id.photos_rc);
        photos_rc.setItemAnimator(new DefaultItemAnimator());
        btnAddPhoto = (ImageButton) findViewById(R.id.btn_add_photo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pickLocation = (RelativeLayout) findViewById(R.id.characteristic_location);

        pickLocation.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);

    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Divespot");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_LOCATION ) {
            if (resultCode == RESULT_OK) {
                this.diveSpotLocation = data.getParcelableExtra("LATLNG");
                locationTitle.setTextColor(getResources().getColor(R.color.black_text));
            }
        }
        if (requestCode == RC_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                imageUris.add(getRealPathFromURI(AddDiveSpotActivity.this, uri));
                photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris, AddDiveSpotActivity.this, addPhotoTitle));
                Log.i(TAG, getRealPathFromURI(AddDiveSpotActivity.this, uri));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(i, RC_PICK_PHOTO);
                break;
            case R.id.characteristic_location:
                Intent intent = new Intent(AddDiveSpotActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, RC_PICK_LOCATION);
               // PickLocationActivity.show(AddDiveSpotActivity.this);
                break;
            case R.id.btn_add_sealife:
                Intent sealifeIntent = new Intent(AddDiveSpotActivity.this, SearchSealifeActivity.class);
                startActivityForResult(sealifeIntent, 1000);
                break;
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
