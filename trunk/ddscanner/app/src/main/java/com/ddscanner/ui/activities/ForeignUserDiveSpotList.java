package com.ddscanner.ui.activities;

import android.app.Activity;
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
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.DiveSpotsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class ForeignUserDiveSpotList extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener{

    private RecyclerView rc;
    private Toolbar toolbar;
    private ProgressView progressBarFull;
    private String toolbarTitle;
    private String userId;
    private boolean isEdited;
    private boolean isCreated;
    private boolean isCheckIn;

    private GetListOfDiveSpotsListener getAddedListener = new GetListOfDiveSpotsListener(ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_ADDED);
    private GetListOfDiveSpotsListener getCheckinsListener = new GetListOfDiveSpotsListener(ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_CHECKINS);
    private GetListOfDiveSpotsListener getEditedListener = new GetListOfDiveSpotsListener(ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_EDITED);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
        userId = getIntent().getStringExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_USER_ID);
        isEdited = getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISEDITED, false);
        isCreated = getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCREATED, false);
        isCheckIn = getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCHECKIN, false);
        findViews();
        if (isEdited) {
            toolbarTitle = getString(R.string.edited);
            DDScannerApplication.getDdScannerRestClient().getEditedDiveSpots(userId, getEditedListener);
        }
        if (isCreated) {
            toolbarTitle = getString(R.string.created);
            DDScannerApplication.getDdScannerRestClient().getAddedDiveSpots(userId, getAddedListener);
        }
        if (isCheckIn) {
            toolbarTitle = getString(R.string.toolbar_title_check_ins);
            DDScannerApplication.getDdScannerRestClient().getUsersCheckins(userId, getCheckinsListener);
        }
        toolbarSettings();
    }

    private void findViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        rc.setLayoutManager(linearLayoutManager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBar);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
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

    public static void show(Activity context, boolean isEdited, boolean isCreated, boolean isCheckin, String userId) {
        Intent intent = new Intent(context, ForeignUserDiveSpotList.class);
        intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_USER_ID, userId);
        if (isEdited) {
            intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISEDITED, true);
        }
        if (isCreated) {
            intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCREATED, true);
        }
        if (isCheckin) {
            intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCHECKIN, true);
        }
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_ADDED:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getDdScannerRestClient().getAddedDiveSpots(userId, getAddedListener);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_EDITED:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getDdScannerRestClient().getEditedDiveSpots(userId, getEditedListener);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN_TO_GET_CHECKINS:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getDdScannerRestClient().getUsersCheckins(userId, getCheckinsListener);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
        }
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

    private class GetListOfDiveSpotsListener implements DDScannerRestClient.ResultListener<DivespotsWrapper> {

        private int requestCodeForLogin;

        GetListOfDiveSpotsListener(int requestCodeForLogin) {
            this.requestCodeForLogin = requestCodeForLogin;
        }

        @Override
        public void onSuccess(DivespotsWrapper result) {
            progressBarFull.setVisibility(View.GONE);
            rc.setVisibility(View.VISIBLE);
            rc.setAdapter(new DiveSpotsListAdapter((ArrayList<DiveSpot>) result.getDiveSpots(), ForeignUserDiveSpotList.this, EventsTracker.SpotViewSource.FROM_PROFILE_CHECKINS));
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_FOREIGN_USER_DIVE_SPOTS_ACTIVITY_FAILED_TO_CONNECT, false);

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    SharedPreferenceHelper.logout();
                    LoginActivity.showForResult(ForeignUserDiveSpotList.this, requestCodeForLogin);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_FOREIGN_USER_DIVE_SPOTS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_FOREIGN_USER_DIVE_SPOTS_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }
}
