package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.GalleryOpenedSource;
import com.ddscanner.entities.PhotoAuthor;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.photos.AllPhotosDiveSpotAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class PhotosGalleryActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private RecyclerView mapsRecyclerView;
    private Toolbar toolbar;
    private ProgressView progressView;
    private String loadedInfoId;
    private GalleryOpenedSource source;
    private PhotoAuthor photoAuthor;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotPhoto> result) {
            if (source.equals(GalleryOpenedSource.MAPS)) {
                mapsRecyclerView.setAdapter(new AllPhotosDiveSpotAdapter(result, PhotosGalleryActivity.this, PhotoOpenedSource.DIVESPOT, true));
            } else {
                for (DiveSpotPhoto diveSpotPhoto : result) {
                    result.get(result.indexOf(diveSpotPhoto)).setAuthor(photoAuthor);
                }
                mapsRecyclerView.setAdapter(new AllPhotosDiveSpotAdapter(result, PhotosGalleryActivity.this, PhotoOpenedSource.DIVESPOT, false));
            }
            progressView.setVisibility(View.GONE);
            mapsRecyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_MAPS_ACTIVITY_FAILED, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_MAPS_ACTIVITY_FAILED, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_MAPS_ACTIVITY_FAILED, false);
        }

    };

    public static void show(String diveSpotId, Context context, GalleryOpenedSource source, String author) {
        Intent intent = new Intent(context, PhotosGalleryActivity.class);
        intent.putExtra("id", diveSpotId);
        intent.putExtra("source", source);
        if (author != null) {
            intent.putExtra("user", author);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_photos);
        mapsRecyclerView = (RecyclerView) findViewById(R.id.maps_rv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressView = (ProgressView) findViewById(R.id.progress_view);
        source = (GalleryOpenedSource) getIntent().getSerializableExtra("source");
        loadedInfoId = getIntent().getStringExtra("id");
        setupToolbar(R.string.photos, R.id.toolbar);
        setupRecyclerView();
        switch (source) {
            case USER_PROFILE:
                photoAuthor = new Gson().fromJson(getIntent().getStringExtra("user"), PhotoAuthor.class);
                DDScannerApplication.getInstance().getDdScannerRestClient().getUserAddedPhotos(resultListener, loadedInfoId);
                break;
            case MAPS:
                DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotMaps(loadedInfoId, resultListener);
                break;
        }
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mapsRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
