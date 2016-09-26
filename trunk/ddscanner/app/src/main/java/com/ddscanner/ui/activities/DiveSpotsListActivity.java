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
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveSpotsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DiveSpotsListActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener {

    private static final String BUNDLE_KEY_SPOT_VIEW_SOURCE = "BUNDLE_KEY_SPOT_VIEW_SOURCE";
    
    private RecyclerView rc;
    private Toolbar toolbar;
    private List<DiveSpot> diveSpots = new ArrayList<>();
    private boolean isAdded = false;
    private ProgressView progressBarFull;
    private EventsTracker.SpotViewSource spotViewSource;

    private GetEditedAddedDiveSpotsResultListener getEditedDiveSpotsResultListener = new GetEditedAddedDiveSpotsResultListener();
    private GetEditedAddedDiveSpotsResultListener getAddedDiveSpotsResultListener = new GetEditedAddedDiveSpotsResultListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
        isAdded = getIntent().getBooleanExtra("ISADDED", false);
        spotViewSource = EventsTracker.SpotViewSource.getByName(getIntent().getStringExtra(BUNDLE_KEY_SPOT_VIEW_SOURCE));
        if (isAdded) {
            DDScannerApplication.getDdScannerRestClient().getAddedDiveSpots(SharedPreferenceHelper.getUserServerId(), getAddedDiveSpotsResultListener);
        } else {
            DDScannerApplication.getDdScannerRestClient().getEditedDiveSpots(SharedPreferenceHelper.getUserServerId(), getEditedDiveSpotsResultListener);
        }
        findViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOTS_LIST_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isAdded) {
                        DDScannerApplication.getDdScannerRestClient().getAddedDiveSpots(SharedPreferenceHelper.getUserServerId(), getAddedDiveSpotsResultListener);
                    } else {
                        DDScannerApplication.getDdScannerRestClient().getEditedDiveSpots(SharedPreferenceHelper.getUserServerId(), getEditedDiveSpotsResultListener);
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    finish();
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
        if (isAdded) {
            getSupportActionBar().setTitle(R.string.added_ds);
        } else {
            getSupportActionBar().setTitle(R.string.edited_ds);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    private void setUi() {
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setAdapter(new DiveSpotsListAdapter((ArrayList<DiveSpot>) diveSpots, this, spotViewSource));
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

    public static void show(Context context, boolean isAdded, EventsTracker.SpotViewSource spotViewSource) {
        Intent intent = new Intent(context, DiveSpotsListActivity.class);
        intent.putExtra("ISADDED", isAdded);
        intent.putExtra(BUNDLE_KEY_SPOT_VIEW_SOURCE, spotViewSource.getName());
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

    private class GetEditedAddedDiveSpotsResultListener implements DDScannerRestClient.ResultListener<DivespotsWrapper> {

        @Override
        public void onSuccess(DivespotsWrapper result) {
            DiveSpotsListActivity.this.diveSpots = result.getDiveSpots();
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    SocialNetworks.showForResult(DiveSpotsListActivity.this, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOTS_LIST_ACTIVITY_LOGIN);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_DIVE_SPOTS_LIST_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
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


