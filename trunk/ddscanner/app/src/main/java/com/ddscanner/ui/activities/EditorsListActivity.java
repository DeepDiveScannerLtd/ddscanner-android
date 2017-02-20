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

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.User;
import com.ddscanner.entities.UserOld;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.EditorsUsersListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;

import java.util.ArrayList;

public class EditorsListActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private RecyclerView usersRecyclerView;
    private User creator;
    private ArrayList<User> users = new ArrayList<>();

    private DDScannerRestClient.ResultListener<ArrayList<User>> usersResultListener = new DDScannerRestClient.ResultListener<ArrayList<User>>() {
        @Override
        public void onSuccess(ArrayList<User> result) {
            users.addAll(result);
            setUi(users);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDITORS_ATIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDITORS_ATIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDITORS_ATIVITY_HIDE, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_checkin);
        findViews();
        setupToolbar(R.string.people, R.id.toolbar);
        creator = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        users.add(creator);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotEditors(usersResultListener, getIntent().getStringExtra("id"));

    }

    private void findViews() {
        usersRecyclerView = (RecyclerView) findViewById(R.id.peoples_rc);
    }

    private void setUi(ArrayList<User> users) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
        usersRecyclerView.setAdapter(new EditorsUsersListAdapter(this, users));
    }

    public static void show(Context context, String id, String creator) {
        Intent intent = new Intent(context, EditorsListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("user", creator);
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
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
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
