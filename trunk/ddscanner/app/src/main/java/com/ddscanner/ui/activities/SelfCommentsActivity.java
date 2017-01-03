package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.CommentOld;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.SelfCommentEntity;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.SelfReviewsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class SelfCommentsActivity extends AppCompatActivity implements InfoDialogFragment.DialogClosedListener {

    private ArrayList<CommentOld> commentOlds;
    private RecyclerView commentsRc;
    private ProgressView progressView;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private String commentToDelete;

    private DDScannerRestClient.ResultListener<ArrayList<SelfCommentEntity>> commentsResultListener = new DDScannerRestClient.ResultListener<ArrayList<SelfCommentEntity>>() {
        @Override
        public void onSuccess(ArrayList<SelfCommentEntity> result) {
           // Comments comments = result;
            progressView.setVisibility(View.GONE);
            commentsRc.setVisibility(View.VISIBLE);
           // path = comments.getDiveSpotPathMedium();
            commentsRc.setAdapter(new SelfReviewsListAdapter(result, SelfCommentsActivity.this));
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SELF_COMMENTS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(SelfCommentsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_VIEW_COMMENTS);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SELF_COMMENTS_ACTIVITY_UNKNOWN_ERROR, false);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<Void> deleteCommentResulListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            getComments();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_SELF_COMMENTS_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_SELF_COMMENTS_ACTIVITY_COMMENT_NOT_FOUND, false);
                    break;
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(SelfCommentsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_DELETE_COMMENTS);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_SELF_COMMENTS_ACTIVITY_UNKNOWN_ERROR, false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        findViews();
        getComments();
    }

    private void findViews() {
        commentsRc = (RecyclerView) findViewById(R.id.reviews_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        leaveReview = (FloatingActionButton) findViewById(R.id.fab_write_review);
        leaveReview.setVisibility(View.GONE);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        setUi();
    }
    private void setUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentsRc.setLayoutManager(linearLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setTitle(R.string.reviews);
    }
    private void getComments() {
        commentsRc.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        DDScannerApplication.getInstance().getDdScannerRestClient().getUsersSelfComments(commentsResultListener);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context, SelfCommentsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_VIEW_COMMENTS:
                if (resultCode == RESULT_OK) {
                    getComments();
                }
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_DELETE_COMMENTS:
                if (resultCode == RESULT_OK) {
                    deleteUsersComment(commentToDelete);
                }
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_EDIT_MY_REVIEW:
                if (resultCode == RESULT_OK) {
                    getComments();
                }
                if (resultCode == RESULT_CANCELED && !DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
                    finish();
                }

        }
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

    private void deleteUsersComment(String id) {
        commentToDelete = id;
        DDScannerApplication.getInstance().getDdScannerRestClient().postDeleteReview(deleteCommentResulListener, id);
    }

    @Subscribe
    public void showLoginActivity(ShowLoginActivityIntent event) {
       // SocialNetworks.showForResult(SelfCommentsActivity.this, RC_LOGIN);
    }

    @Subscribe
    public void deleteComment(DeleteCommentEvent event) {
        deleteUsersComment(String.valueOf(event.getCommentId()));
    }

    @Subscribe
    public void editComment(EditCommentEvent editCommentEvent) {
        EditCommentActivity.showForResult(this, editCommentEvent.getComment(), ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_EDIT_MY_REVIEW);
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
        switch (requestCode) {
            case DialogsRequestCodes.DRC_SELF_COMMENTS_ACTIVITY_UNKNOWN_ERROR:
            case DialogsRequestCodes.DRC_SELF_COMMENTS_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DELETED_COMMENT_NOT_FOUND:
                getComments();
                break;
        }
    }
}
