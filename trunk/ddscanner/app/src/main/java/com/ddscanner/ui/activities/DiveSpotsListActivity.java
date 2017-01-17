package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.DiveSpotsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class DiveSpotsListActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener {

    private static final String BUNDLE_KEY_SPOT_VIEW_SOURCE = "BUNDLE_KEY_SPOT_VIEW_SOURCE";
    
    private RecyclerView rc;
    private Toolbar toolbar;
    private List<DiveSpotShort> diveSpotShorts = new ArrayList<>();
    private boolean isAdded = false;
    private ProgressView progressBarFull;
    private EventsTracker.SpotViewSource spotViewSource;
    private DiveSpotListSource diveSpotListSource;
    private String userId;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>> divespotsWrapperResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>>() {

        @Override
        public void onSuccess(ArrayList<DiveSpotShort> result) {
            DiveSpotsListActivity.this.diveSpotShorts = result;
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
        userId = getIntent().getStringExtra("id");
        diveSpotListSource = (DiveSpotListSource) getIntent().getSerializableExtra("source");
        switch (diveSpotListSource) {
            case ADDED:
                DDScannerApplication.getInstance().getDdScannerRestClient().getAddedDiveSpots(divespotsWrapperResultListener, userId);
                break;
            case EDITED:
                DDScannerApplication.getInstance().getDdScannerRestClient().getEditedDiveSpots(divespotsWrapperResultListener, userId);
                break;
            case FAVORITES:
                DDScannerApplication.getInstance().getDdScannerRestClient().getUsersFavourites(divespotsWrapperResultListener, userId);
                break;
            case CHECKINS:
                DDScannerApplication.getInstance().getDdScannerRestClient().getUsersCheckins(divespotsWrapperResultListener, userId);
                break;
        }
        findViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOTS_LIST_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    switch (diveSpotListSource) {
                        case ADDED:
                            DDScannerApplication.getInstance().getDdScannerRestClient().getAddedDiveSpots(divespotsWrapperResultListener, userId);
                            break;
                        case EDITED:
                            DDScannerApplication.getInstance().getDdScannerRestClient().getEditedDiveSpots(divespotsWrapperResultListener, userId);
                            break;
                        case FAVORITES:
                            DDScannerApplication.getInstance().getDdScannerRestClient().getUsersFavourites(divespotsWrapperResultListener, userId);
                            break;
                        case CHECKINS:
                            DDScannerApplication.getInstance().getDdScannerRestClient().getUsersCheckins(divespotsWrapperResultListener, userId);
                            break;
                    }
                    if (resultCode == RESULT_CANCELED) {
                        finish();
                    }
                }
                break;
        }
    }
    
    private void findViews() {
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "";
        switch (diveSpotListSource) {
            case ADDED:
                title = getString(R.string.added_ds);
                break;
            case EDITED:
                title = getString(R.string.edited_ds);
                break;
            case CHECKINS:
                title = getString(R.string.checkin);
                break;
            case FAVORITES:
                title =getString(R.string.favorites);
                break;
        }
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    private void setUi() {
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setAdapter(new DiveSpotsListAdapter((ArrayList<DiveSpotShort>) diveSpotShorts, this, spotViewSource));
        progressBarFull.setVisibility(View.GONE);
        rc.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static void show(Context context, DiveSpotListSource diveSpotListSource, String userId) {
        Intent intent = new Intent(context, DiveSpotsListActivity.class);
        intent.putExtra("source", diveSpotListSource);
        intent.putExtra("id", userId);
        context.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }
}


