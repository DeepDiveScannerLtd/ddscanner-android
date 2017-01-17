package com.ddscanner.screens.instructors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.Instructor;
import com.ddscanner.events.RemoveInstructorEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class InstructorsActivity extends BaseAppCompatActivity implements InfoDialogFragment.DialogClosedListener {

    private RecyclerView recyclerView;
    private ProgressView progressView;
    private InstructorListAdapter instructorListAdapter;
    private MaterialDialog materialDialog;
    private int removedInstructorId;

    private DDScannerRestClient.ResultListener<Void> removeResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            instructorListAdapter.remove(removedInstructorId);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    private DDScannerRestClient.ResultListener<ArrayList<Instructor>> resultListener = new DDScannerRestClient.ResultListener<ArrayList<Instructor>>() {
        @Override
        public void onSuccess(ArrayList<Instructor> result) {
            instructorListAdapter = new InstructorListAdapter(result, InstructorsActivity.this);
            recyclerView.setAdapter(instructorListAdapter);

            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_INSTRUCTORS_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_INSTRUCTORS_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_INSTRUCTORS_ACTIVITY_HIDE, false);
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
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressView = (ProgressView) findViewById(R.id.progress_bar);
        materialDialog = Helpers.getMaterialDialog(this);
        setupToolbar(R.string.instructors, R.id.toolbar);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenterInstructorsList(resultListener, getIntent().getStringExtra("id"));
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
        DDScannerApplication.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void removeInstructorClicked(RemoveInstructorEvent event) {
        materialDialog.show();
        removedInstructorId = event.getPosition();
        DDScannerApplication.getInstance().getDdScannerRestClient().postRemoveInstructorFromDivecenter(removeResultListener, event.getId());
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
