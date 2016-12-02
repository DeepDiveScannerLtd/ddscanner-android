package com.ddscanner.screens.divespot.photos;

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
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.rest.DDScannerRestClient;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class DiveSpotMapsActivity extends AppCompatActivity {

    private RecyclerView mapsRecyclerView;
    private Toolbar toolbar;
    private ProgressView progressView;
    private String diveSpotId;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotPhoto> result) {
            mapsRecyclerView.setAdapter(new AllPhotosDiveSpotAdapter(result, DiveSpotMapsActivity.this, diveSpotId, PhotoOpenedSource.DIVESPOT));
            progressView.setVisibility(View.GONE);
            mapsRecyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }
    };

    public static void show(String diveSpotId, Context context) {
        Intent intent = new Intent(context, DiveSpotMapsActivity.class);
        intent.putExtra("id", diveSpotId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_photos);
        mapsRecyclerView = (RecyclerView) findViewById(R.id.maps_rv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressView = (ProgressView) findViewById(R.id.progress_view);
        diveSpotId = getIntent().getStringExtra("id");
        setupToolbar();
        setupRecyclerView();
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotMaps(diveSpotId, resultListener);
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mapsRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.maps_toolbar_title);
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
}
