package com.ddscanner.screens.instructors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Instructor;
import com.ddscanner.events.RemoveInstructorEvent;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.ConfirmationDialogFragment;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class InstructorsActivity extends BaseAppCompatActivity implements DialogClosedListener, ConfirmationDialogClosedListener {

    private RecyclerView recyclerView;
    private ProgressView progressView;
    private InstructorListAdapter instructorListAdapter;
    private MaterialDialog materialDialog;
    private int removedInstructorId;
    private String userId;
    private String instructorForRemoveId;

    private DDScannerRestClient.ResultListener<Void> removeResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            instructorListAdapter.remove(removedInstructorId);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    private DDScannerRestClient.ResultListener<ArrayList<Instructor>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<Instructor>>() {
        @Override
        public void onSuccess(ArrayList<Instructor> result) {
            instructorListAdapter = new InstructorListAdapter(result, InstructorsActivity.this, DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId().equals(userId));
            recyclerView.setAdapter(instructorListAdapter);

            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_INSTRUCTORS_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_INSTRUCTORS_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_INSTRUCTORS_ACTIVITY_HIDE, false);
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
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = findViewById(R.id.progress_bar);
        materialDialog = Helpers.getMaterialDialog(this);
        userId = getIntent().getStringExtra("id");
        setupToolbar(R.string.instructors, R.id.toolbar);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterInstructorsList(resultListener, userId);
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
        if (instructorListAdapter != null && instructorListAdapter.getShowedInstructors().size() > 0) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("ids", instructorListAdapter.getShowedInstructors());
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        DDScannerApplication.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void removeInstructorClicked(RemoveInstructorEvent event) {
        removedInstructorId = event.getPosition();
        instructorForRemoveId = event.getId();
        ConfirmationDialogFragment.showForActivity(getSupportFragmentManager(), R.string.empty_string, getString(R.string.remove_instructor_confriamtion, event.getName()), R.string.yes, R.string.no);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Override
    public void onPositiveDialogClicked() {
        materialDialog.show();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postRemoveInstructorFromDivecenter(removeResultListener, instructorForRemoveId);

    }

    @Override
    public void onNegativeDialogClicked() {

    }
}
