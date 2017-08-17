package com.ddscanner.screens.divespots.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.events.ShowDIveSpotDetailsActivityEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class DiveSpotsListActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private static final String BUNDLE_KEY_SPOT_VIEW_SOURCE = "BUNDLE_KEY_SPOT_VIEW_SOURCE";
    
    private RecyclerView rc;
    private Toolbar toolbar;
    private List<DiveSpotShort> diveSpotShorts = new ArrayList<>();
    private boolean isAdded = false;
    private ProgressView progressBarFull;
    private EventsTracker.SpotViewSource spotViewSource;
    private DiveSpotListSource diveSpotListSource;
    private String userId;
    private DiveSpotsListAdapter diveSpotsListAdapter;
    private int positionToDelete;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>> divespotsListResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>>() {

        @Override
        public void onSuccess(ArrayList<DiveSpotShort> result) {
            DiveSpotsListActivity.this.diveSpotShorts = result;
            diveSpotsListAdapter = new DiveSpotsListAdapter(result, DiveSpotsListActivity.this, spotViewSource);
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
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
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getAddedDiveSpots(divespotsListResultListener, userId);
                break;
            case EDITED:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getEditedDiveSpots(divespotsListResultListener, userId);
                break;
            case FAVORITES:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getUsersFavourites(divespotsListResultListener, userId);
                break;
            case CHECKINS:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getUsersCheckins(divespotsListResultListener, userId);
                break;
            case APPROVE:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotsForApprove(divespotsListResultListener);
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
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getAddedDiveSpots(divespotsListResultListener, userId);
                            break;
                        case EDITED:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getEditedDiveSpots(divespotsListResultListener, userId);
                            break;
                        case FAVORITES:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUsersFavourites(divespotsListResultListener, userId);
                            break;
                        case CHECKINS:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUsersCheckins(divespotsListResultListener, userId);
                            break;
                        case APPROVE:
                            DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotsForApprove(divespotsListResultListener);
                            break;
                    }
                    if (resultCode == RESULT_CANCELED) {
                        finish();
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOTS_LIST_ADAPTER:
                if (resultCode == RESULT_CODE_DIVE_SPOT_REMOVED) {
                    diveSpotsListAdapter.removeSpotFromList(positionToDelete);
                }
                break;
        }
    }
    
    private void findViews() {
        rc = findViewById(R.id.divespots_rc);
        toolbar = findViewById(R.id.toolbar);
        progressBarFull = findViewById(R.id.progressBar);
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
            case APPROVE:
                title = getString(R.string.dive_spot_approve);
        }
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    private void setUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setAdapter(diveSpotsListAdapter);
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
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe
    public void showDiveSpotDetailsActivity(ShowDIveSpotDetailsActivityEvent event) {
        positionToDelete = event.getPosition();
        DiveSpotDetailsActivity.showForResult(this, event.getId(), spotViewSource, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOTS_LIST_ADAPTER);
    }

}


