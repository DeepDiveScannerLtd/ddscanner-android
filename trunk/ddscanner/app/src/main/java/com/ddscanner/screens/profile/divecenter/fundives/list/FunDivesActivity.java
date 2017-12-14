package com.ddscanner.screens.profile.divecenter.fundives.list;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.FunDive;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class FunDivesActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String ARG_ID = "id";

    DDScannerRestClient.ResultListener<ArrayList<FunDive>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<FunDive>>() {
        @Override
        public void onSuccess(ArrayList<FunDive> result) {
            funDivesListAdapter.setFunDives(result);
            progressView.setVisibility(View.GONE);
            fundDivesList.setVisibility(View.VISIBLE);
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

    private RecyclerView fundDivesList;
    private FunDivesListAdapter funDivesListAdapter;
    private String diveCenterId;
    private ProgressView progressView;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, FunDivesActivity.class);
        intent.putExtra(ARG_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divecenter_daily_tours);
        findViews();
        setupList();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterFunDives(resultListener, getIntent().getLongExtra(ARG_ID, 0));
    }

    private void findViews() {
        fundDivesList = findViewById(R.id.tours_list);
        progressView = findViewById(R.id.progressBar);
    }

    private void setupList() {
        funDivesListAdapter = new FunDivesListAdapter(item -> {

        });
        fundDivesList.setLayoutManager(new LinearLayoutManager(this));
        fundDivesList.setAdapter(funDivesListAdapter);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
