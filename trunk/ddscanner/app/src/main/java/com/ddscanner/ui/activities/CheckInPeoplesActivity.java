package com.ddscanner.ui.activities;

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
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.User;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.UserListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

/**
 * Created by lashket on 28.4.16.
 */
public class CheckInPeoplesActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private RecyclerView usersRecyclerView;
    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<ArrayList<User>> usersResultListener = new DDScannerRestClient.ResultListener<ArrayList<User>>() {

        @Override
        public void onSuccess(ArrayList<User> result) {
            setUi(result);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_CHECKINS_ACTIVITY_HIDE_ACTIVITY, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_CHECKINS_ACTIVITY_HIDE_ACTIVITY, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_CHECKINS_ACTIVITY_HIDE_ACTIVITY, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventsTracker.trackDiveSpotCheckinsView();
        setContentView(R.layout.activity_peoples_checkin);
        findViews();
        setupToolbar(R.string.people, R.id.toolbar);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotsCheckedInUsers(usersResultListener, getIntent().getStringExtra("id"));
    }

    private void findViews() {
        progressView = (ProgressView) findViewById(R.id.progress_bar);
        usersRecyclerView = (RecyclerView) findViewById(R.id.peoples_rc);
    }

    private void setUi(ArrayList<User> users) {
        progressView.setVisibility(View.GONE);
        usersRecyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.setAdapter(new UserListAdapter(this, users));
    }

    public static void show(Context context, String diveSpotId) {
        Intent intent = new Intent(context, CheckInPeoplesActivity.class);
        intent.putExtra("id", diveSpotId);
        context.startActivity(intent);
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

    @Override
    public void onStart() {
        super.onStart();
//        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
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
        finish();
    }
}
