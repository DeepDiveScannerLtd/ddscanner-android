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
import com.ddscanner.entities.ForeignUserDislikesWrapper;
import com.ddscanner.entities.ForeignUserLike;
import com.ddscanner.entities.ForeignUserLikeWrapper;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.ForeignUserLikesAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

public class UserLikesDislikesActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private boolean isLikes;
    private String userId;
    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<ForeignUserLikeWrapper> foreignUserLikeWrapperResultListener = new DDScannerRestClient.ResultListener<ForeignUserLikeWrapper>() {
        @Override
        public void onSuccess(ForeignUserLikeWrapper result) {
            recyclerView.setAdapter(new ForeignUserLikesAdapter(UserLikesDislikesActivity.this, (ArrayList<ForeignUserLike>) result.getLikes(), true));
            recyclerView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(UserLikesDislikesActivity.this, ActivitiesRequestCodes.REQUEST_CODE_USER_LIKES_DISLIKES_ACTIVITY_LOGIN);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<ForeignUserDislikesWrapper> foreignUserDislikesWrapperResultListener = new DDScannerRestClient.ResultListener<ForeignUserDislikesWrapper>() {
        @Override
        public void onSuccess(ForeignUserDislikesWrapper result) {
            recyclerView.setAdapter(new ForeignUserLikesAdapter(UserLikesDislikesActivity.this, (ArrayList<ForeignUserLike>) result.getDislikes(), false));
            recyclerView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(UserLikesDislikesActivity.this, ActivitiesRequestCodes.REQUEST_CODE_USER_LIKES_DISLIKES_ACTIVITY_LOGIN);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_user_likes_dislikes);
        isLikes = getIntent().getBooleanExtra(Constants.USER_LIKES_ACTIVITY_INTENT_IS_LIKE, false);
        userId = getIntent().getStringExtra(Constants.USER_LIKES_ACTIVITY_INTENT_USER_ID);
        findViews();
        if (isLikes) {
            DDScannerApplication.getInstance().getDdScannerRestClient().getUserLikes(userId, foreignUserLikeWrapperResultListener);
        } else {
            DDScannerApplication.getInstance().getDdScannerRestClient().getUserDislikes(userId, foreignUserDislikesWrapperResultListener);
        }
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.likesRecyclerView);
        progressView = (ProgressView) findViewById(R.id.progressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        toolbarSettings();
    }
    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        if (!isLikes) {
            getSupportActionBar().setTitle(R.string.user_dislikes);
        } else {
            getSupportActionBar().setTitle(R.string.user_likes);
        }
    }



    public static void showForResult(Activity context, boolean isLikes, String userId, int requestCode) {
        Intent intent = new Intent(context, UserLikesDislikesActivity.class);
        intent.putExtra(Constants.USER_LIKES_ACTIVITY_INTENT_IS_LIKE, isLikes);
        intent.putExtra(Constants.USER_LIKES_ACTIVITY_INTENT_USER_ID, userId);
        context.startActivityForResult(intent, requestCode);
    }

    public static void show(Activity context, boolean isLikes, String userId) {
        Intent intent = new Intent(context, UserLikesDislikesActivity.class);
        intent.putExtra(Constants.USER_LIKES_ACTIVITY_INTENT_IS_LIKE, isLikes);
        intent.putExtra(Constants.USER_LIKES_ACTIVITY_INTENT_USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_USER_LIKES_DISLIKES_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isLikes) {
                        DDScannerApplication.getInstance().getDdScannerRestClient().getUserLikes(userId, foreignUserLikeWrapperResultListener);
                    } else {
                        DDScannerApplication.getInstance().getDdScannerRestClient().getUserDislikes(userId, foreignUserDislikesWrapperResultListener);
                    }
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
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
            case DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }
}
