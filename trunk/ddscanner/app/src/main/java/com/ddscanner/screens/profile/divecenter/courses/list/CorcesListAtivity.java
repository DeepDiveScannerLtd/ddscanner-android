package com.ddscanner.screens.profile.divecenter.courses.list;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.entities.CourseDetails;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.divecenter.courses.details.CourseDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class CorcesListAtivity extends BaseAppCompatActivity implements DialogClosedListener {

    private DDScannerRestClient.ResultListener<ArrayList<CourseDetails>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<CourseDetails>>() {
        @Override
        public void onSuccess(ArrayList<CourseDetails> result) {
            progressView.setVisibility(View.GONE);
            courcesList.setVisibility(View.VISIBLE);
            courcesListAdapter.setCources(result);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }

        @Override
        public void onInternetConnectionClosed() {

        }
    };

    CourcesListAdapter courcesListAdapter;
    ProgressView progressView;
    RecyclerView courcesList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divecenter_daily_tours);
        findViews();
        setupList();
    }

    private void setupList() {
        courcesList.setLayoutManager(new LinearLayoutManager(this));
        courcesListAdapter = new CourcesListAdapter(item -> CourseDetailsActivity.show(this, item.getId()));
        courcesList.setAdapter(courcesListAdapter);
    }

    private void findViews() {
        courcesList = findViewById(R.id.tours_list);
        progressView = findViewById(R.id.progressBar);
    }

    @Override
    public void onDialogClosed(int requestCode) {

    }
}
