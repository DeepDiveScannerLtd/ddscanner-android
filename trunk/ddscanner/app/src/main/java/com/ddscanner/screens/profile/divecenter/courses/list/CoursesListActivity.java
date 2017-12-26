package com.ddscanner.screens.profile.divecenter.courses.list;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.CourseDetails;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.divecenter.courses.details.CourseDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class CoursesListActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String ARG_ID = "id";

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, CoursesListActivity.class);
        intent.putExtra(ARG_ID, id);
        context.startActivity(intent);
    }

    private DDScannerRestClient.ResultListener<ArrayList<CourseDetails>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<CourseDetails>>() {
        @Override
        public void onSuccess(ArrayList<CourseDetails> result) {
            progressView.setVisibility(View.GONE);
            courcesList.setVisibility(View.VISIBLE);
            coursesListAdapter.setCources(result);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, 1, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, 1, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, 1, false);
        }
    };

    CoursesListAdapter coursesListAdapter;
    ProgressView progressView;
    RecyclerView courcesList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divecenter_daily_tours);
        setupToolbar(R.string.courses, R.id.toolbar);
        findViews();
        setupList();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterCourses(resultListener, getIntent().getLongExtra(ARG_ID, -1));
    }

    private void setupList() {
        courcesList.setLayoutManager(new LinearLayoutManager(this));
        coursesListAdapter = new CoursesListAdapter(item -> CourseDetailsActivity.show(this, item.getId()));
        courcesList.setAdapter(coursesListAdapter);
    }

    private void findViews() {
        courcesList = findViewById(R.id.tours_list);
        progressView = findViewById(R.id.progressBar);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
