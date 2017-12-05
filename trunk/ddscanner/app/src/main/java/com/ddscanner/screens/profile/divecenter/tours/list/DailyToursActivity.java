package com.ddscanner.screens.profile.divecenter.tours.list;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DailyTour;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.profile.divecenter.tours.details.TourDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;

import java.util.ArrayList;

public class DailyToursActivity extends BaseAppCompatActivity {

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

    private RecyclerView toursList;
    private DailyToursListAdapter dailyToursListAdapter;
    private String diveCenterId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divecenter_daily_tours);
        toursList = findViewById(R.id.tours_list);
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
}
