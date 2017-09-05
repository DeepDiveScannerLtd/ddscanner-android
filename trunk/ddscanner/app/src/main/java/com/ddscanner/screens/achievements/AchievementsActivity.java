package com.ddscanner.screens.achievements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.AchievementTitle;
import com.ddscanner.entities.AchievmentsResponseEntity;
import com.ddscanner.entities.CompleteAchievement;
import com.ddscanner.entities.PendingAchievement;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogsRequestCodes;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AchievmentsResponseEntity achievmentsResponseEntity;
    private List<CompleteAchievement> completeAchievements;
    private List<PendingAchievement> pendingAchievements;
    private ProgressView progressView;
    private String userId;
    private RelativeLayout noAchievementsView;
    private AchievementTitleListAdapter achievementTitleListAdapter = new AchievementTitleListAdapter();

    private DDScannerRestClient.ResultListener<ArrayList<AchievementTitle>> responseEntityResultListener = new DDScannerRestClient.ResultListener<ArrayList<AchievementTitle>>() {
        @Override
        public void onSuccess(ArrayList<AchievementTitle> result) {
            progressView.setVisibility(View.GONE);
//            achievmentsResponseEntity = result;
//            completeAchievements = new ArrayList<>();
//            pendingAchievements = new ArrayList<>();
//            if (achievmentsResponseEntity.getPendingAchievements() != null) {
//                pendingAchievements = achievmentsResponseEntity.getPendingAchievements();
//                completeAchievements.addAll(pendingAchievements);
//            }
//            if (achievmentsResponseEntity.getCompleteAchievements() != null) {
//                completeAchievements.addAll(achievmentsResponseEntity.getCompleteAchievements());
//            }
//            if (completeAchievements.size() > 0) {
//                recyclerView.setVisibility(View.VISIBLE);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AchievementsActivity.this);
//                recyclerView.setLayoutManager(linearLayoutManager);
//                recyclerView.setAdapter(new AchievementsActivityListAdapter((ArrayList<CompleteAchievement>) completeAchievements, AchievementsActivity.this));
//            } else {
//                noAchievementsView.setVisibility(View.VISIBLE);
//            }
            if (result.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(AchievementsActivity.this));
                achievementTitleListAdapter.setAchievementTitles(result);
                recyclerView.setAdapter(achievementTitleListAdapter);
                return;
            }
            noAchievementsView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_ACHIEVEMENTS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            if (errorType != DDScannerRestClient.ErrorType.UNAUTHORIZED_401) {
                UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_ACHIEVEMENTS_ACTIVITY_UNEXPECTED_ERROR, false);
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_ACHIEVEMENTS_ACTIVITY_FAILED_TO_CONNECT, false);
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
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserAchivements(responseEntityResultListener);
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        noAchievementsView = findViewById(R.id.no_achievements_view);
        recyclerView = findViewById(R.id.achievments_rv);
        progressView = findViewById(R.id.progressBar);
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
                    DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserAchivements(responseEntityResultListener);
                } else {
                    finish();
                }
                break;
        }
    }
}
