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
import com.ddscanner.entities.AchievmentsResponseEntity;
import com.ddscanner.entities.CompleteAchievement;
import com.ddscanner.entities.PendingAchievement;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.AchievementsActivityListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AchievmentsResponseEntity achievmentsResponseEntity;
    private List<CompleteAchievement> completeAchievements;
    private List<PendingAchievement> pendingAchievements;
    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<AchievmentsResponseEntity> responseEntityResultListener = new DDScannerRestClient.ResultListener<AchievmentsResponseEntity>() {
        @Override
        public void onSuccess(AchievmentsResponseEntity result) {
            progressView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            achievmentsResponseEntity = result;
            completeAchievements = new ArrayList<>();
            pendingAchievements = new ArrayList<>();
            if (achievmentsResponseEntity.getCompleteAchievements() != null) {
                completeAchievements = achievmentsResponseEntity.getCompleteAchievements();
            }
            if (achievmentsResponseEntity.getPendingAchievements() != null) {
                pendingAchievements = achievmentsResponseEntity.getPendingAchievements();
                completeAchievements.addAll(pendingAchievements);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AchievementsActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(new AchievementsActivityListAdapter((ArrayList<CompleteAchievement>) completeAchievements, AchievementsActivity.this));
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_ACHIEVEMENTS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    LoginActivity.showForResult(AchievementsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ACHIEVEMENTS_ACTIVITY_LOGIN_TO_ACHIEVEMNTS);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_ACHIEVEMENTS_ACTIVITY_UNEXPECTED_ERROR, false);
                    break;
            }
        }
    };

    public static void show(Context context) {
        Intent intent = new Intent(context, AchievementsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahievments);
        findViews();
        DDScannerApplication.getDdScannerRestClient().getUserAchievements(SharedPreferenceHelper.getUserServerId(), responseEntityResultListener);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.achievments_rv);
        progressView = (ProgressView) findViewById(R.id.progressBar);
        setUi();
    }

    private void setUi() {

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.achievements);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);

        //RecyclerView
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
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_ACHIEVEMENTS_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_ACHIEVEMENTS_ACTIVITY_UNEXPECTED_ERROR:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ACHIEVEMENTS_ACTIVITY_LOGIN_TO_ACHIEVEMNTS:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getDdScannerRestClient().getUserAchievements(SharedPreferenceHelper.getUserServerId(), responseEntityResultListener);
                } else {
                    finish();
                }
                break;
        }
    }
}
