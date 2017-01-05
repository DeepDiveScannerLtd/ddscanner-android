package com.ddscanner.screens.instructors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Instructor;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class InstructorsActivity extends BaseAppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<ArrayList<Instructor>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<Instructor>>() {
        @Override
        public void onSuccess(ArrayList<Instructor> result) {
            recyclerView.setAdapter(new InstructorListAdapter(result, InstructorsActivity.this));
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }
    };

    public static void showForResult(Activity context, int requestCode, String diveCenterId) {
        Intent intent = new Intent(context, InstructorsActivity.class);
        intent.putExtra("id", diveCenterId);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructors_list);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = (ProgressView) findViewById(R.id.progress_bar);
        setupToolbar(R.string.instructors, R.id.toolbar);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenterInstructorsList(resultListener, getIntent().getStringExtra("id"));
    }
}
