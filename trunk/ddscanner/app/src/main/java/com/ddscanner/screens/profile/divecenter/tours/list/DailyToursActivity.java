package com.ddscanner.screens.profile.divecenter.tours.list;


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
import com.ddscanner.entities.DailyTour;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.divecenter.tours.details.TourDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class DailyToursActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String ARG_ID = "id";

    public static void show(Context context, String diveCenterId) {
        Intent intent = new Intent(context, DailyToursActivity.class);
        intent.putExtra(ARG_ID, diveCenterId);
        context.startActivity(intent);
    }

    private DDScannerRestClient.ResultListener<ArrayList<DailyTour>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<DailyTour>>() {
        @Override
        public void onSuccess(ArrayList<DailyTour> result) {
            dailyToursListAdapter.setData(result);
            progressView.setVisibility(View.GONE);
            toursList.setVisibility(View.VISIBLE);
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

    private RecyclerView toursList;
    private DailyToursListAdapter dailyToursListAdapter;
    private String diveCenterId;
    private ProgressView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divecenter_daily_tours);
        toursList = findViewById(R.id.tours_list);
        progressView = findViewById(R.id.progressBar);
        diveCenterId = getIntent().getStringExtra(ARG_ID);
        setupToolbar(R.string.daily_tours, R.id.toolbar);
        setupList();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterProducts(resultListener, diveCenterId);
    }

    private void setupList() {
        toursList.setLayoutManager(new LinearLayoutManager(this));
        dailyToursListAdapter = new DailyToursListAdapter(item -> {
            TourDetailsActivity.show(this, item.getId());});
        toursList.setAdapter(dailyToursListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
