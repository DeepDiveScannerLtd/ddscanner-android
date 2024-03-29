package com.ddscanner.ui.activities;

import android.app.Activity;
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
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.events.OpenReviewActivityEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.reiews.list.ReviewsActivity;
import com.ddscanner.screens.user.dislikes.DislikesListAdapter;
import com.ddscanner.screens.user.likes.LikesListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class UserLikesDislikesActivity extends BaseAppCompatActivity implements DialogClosedListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private boolean isLikes;
    private String userId;
    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<ArrayList<LikeEntity>> likesResultListener = new DDScannerRestClient.ResultListener<ArrayList<LikeEntity>>() {
        @Override
        public void onSuccess(ArrayList<LikeEntity> result) {
            recyclerView.setAdapter(new LikesListAdapter(result, UserLikesDislikesActivity.this));
            recyclerView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    private DDScannerRestClient.ResultListener<ArrayList<LikeEntity>> dislikesResultListener = new DDScannerRestClient.ResultListener<ArrayList<LikeEntity>>() {
        @Override
        public void onSuccess(ArrayList<LikeEntity> result) {
            recyclerView.setAdapter(new DislikesListAdapter(result, UserLikesDislikesActivity.this));
            recyclerView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT, false);
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_user_likes_dislikes);
        isLikes = getIntent().getBooleanExtra(Constants.USER_LIKES_ACTIVITY_INTENT_IS_LIKE, false);
        userId = getIntent().getStringExtra("id");
        findViews();
        if (isLikes) {
            setupToolbar(R.string.user_likes, R.id.toolbar);
            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserLikes(likesResultListener, userId);
        } else {
            setupToolbar(R.string.user_dislikes, R.id.toolbar);
            DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserDislikes(dislikesResultListener, userId);
        }
    }

    private void findViews() {
        recyclerView = findViewById(R.id.likesRecyclerView);
        progressView = findViewById(R.id.progressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
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
        intent.putExtra("id", userId);
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
                        DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserLikes(likesResultListener, userId);
                    } else {
                        DDScannerApplication.getInstance().getDdScannerRestClient(this).getUserDislikes(dislikesResultListener, userId);
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
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_USER_LIKES_DISLIKES_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }

    @Subscribe
    public void openReviewActivity(OpenReviewActivityEvent event) {
        ReviewsActivity.showForResult(this, event.getReviewId(), -1, ReviewsOpenedSource.SINGLE);
    }
}
