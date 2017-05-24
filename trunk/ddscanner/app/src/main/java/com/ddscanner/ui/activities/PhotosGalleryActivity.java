package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoAuthor;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.photos.AllPhotosDiveSpotAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class PhotosGalleryActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private RecyclerView photosRecyclerView;
    private Toolbar toolbar;
    private ProgressView progressView;
    private String loadedInfoId;
    private PhotoOpenedSource source;
    private PhotoAuthor photoAuthor;
    private boolean isDataChanged = false;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotPhoto> result) {
            AllPhotosDiveSpotAdapter photosDiveSpotAdapter;
            switch (source) {
                case PROFILE:
                    if (photoAuthor.getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                        for (DiveSpotPhoto diveSpotPhoto : result) {
                            result.get(result.indexOf(diveSpotPhoto)).setAuthor(photoAuthor);
                        }
                    } else {
                        for (DiveSpotPhoto diveSpotPhoto : result) {
                            result.get(result.indexOf(diveSpotPhoto)).setAuthor(photoAuthor);
                        }
                    }
                case MAPS:
                case NOTIFICATION:
                case REVIEW:
                    photosDiveSpotAdapter = new AllPhotosDiveSpotAdapter(result, PhotosGalleryActivity.this, source, loadedInfoId);
                    photosRecyclerView.setAdapter(photosDiveSpotAdapter);
                    progressView.setVisibility(View.GONE);
                    photosRecyclerView.setVisibility(View.VISIBLE);
                    break;
            }

        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_MAPS_ACTIVITY_FAILED, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_MAPS_ACTIVITY_FAILED, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_MAPS_ACTIVITY_FAILED, false);
        }

    };

    public static void show(String diveSpotId, Context context, PhotoOpenedSource source, String author) {
        Intent intent = new Intent(context, PhotosGalleryActivity.class);
        intent.putExtra("id", diveSpotId);
        intent.putExtra("source", source);
        if (author != null) {
            intent.putExtra("user", author);
        }
        context.startActivity(intent);
    }

    public static void showForResult(String diveSpotId, Activity context, PhotoOpenedSource source, String author, int requestCode) {
        Intent intent = new Intent(context, PhotosGalleryActivity.class);
        intent.putExtra("id", diveSpotId);
        intent.putExtra("source", source);
        if (author != null) {
            intent.putExtra("user", author);
        }
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_photos);
        photosRecyclerView = (RecyclerView) findViewById(R.id.maps_rv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressView = (ProgressView) findViewById(R.id.progress_view);
        source = (PhotoOpenedSource) getIntent().getSerializableExtra("source");
        loadedInfoId = getIntent().getStringExtra("id");
        switch (source) {
            case MAPS:
                setupToolbar(R.string.maps_toolbar_title, R.id.toolbar);
                break;
            default:
                setupToolbar(R.string.photos, R.id.toolbar);
                break;
        }
        setupRecyclerView();
        switch (source) {
            case PROFILE:
                photoAuthor = new Gson().fromJson(getIntent().getStringExtra("user"), PhotoAuthor.class);
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserAddedPhotos(resultListener, loadedInfoId);
                break;
            case MAPS:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotMaps(loadedInfoId, resultListener);
                break;
            case REVIEW:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getReviewPhotos(resultListener, loadedInfoId);
                break;
            case NOTIFICATION:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getNotificationPhotos(resultListener, loadedInfoId);
        }
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        photosRecyclerView.setLayoutManager(gridLayoutManager);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_PHOTOS_ACTIVITY_SLIDER:
                if (resultCode == RESULT_OK) {
                    isDataChanged = true;
                    switch (source) {
                        case PROFILE:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserAddedPhotos(resultListener, loadedInfoId);
                            break;
                        case MAPS:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotMaps(loadedInfoId, resultListener);
                            break;
                        case REVIEW:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getReviewPhotos(resultListener, loadedInfoId);
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged) {
            setResult(RESULT_OK);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
