package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.GeneralError;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.DislikeCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.IsCommentLikedEvent;
import com.ddscanner.events.LikeCommentEvent;
import com.ddscanner.events.ReportCommentEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.ShowSliderForReviewImagesEvent;
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private ArrayList<Comment> comments;
    private RecyclerView commentsRc;
    private ProgressView progressView;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private String diveSpotId;
    private String commentToDelete;

    private String path;

    private boolean isHasNewComment = false;

    private FiltersResponseEntity filters = new FiltersResponseEntity();
    private ReviewsListAdapter reviewsListAdapter;

    private List<String> reportItems = new ArrayList<>();

    private String reportCommentId;
    private String reportType;
    private String reportDescription = null;
    private MaterialDialog materialDialog;
    private int reviewPositionToRate;
    private boolean isNeedRefreshComments;

    private DDScannerRestClient.ResultListener<FiltersResponseEntity> filtersResponseEntityResultListener = new DDScannerRestClient.ResultListener<FiltersResponseEntity>() {
        @Override
        public void onSuccess(FiltersResponseEntity result) {
            filters = result;
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
        }
    };

    private DDScannerRestClient.ResultListener<Void> likeCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackCommentLiked();
            reviewsListAdapter.commentLiked(reviewPositionToRate);
            isHasNewComment = true;
            if (isNeedRefreshComments) {
                getComments();
                isNeedRefreshComments = !isNeedRefreshComments;
            }
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
                    break;
                case RIGHTS_NOT_FOUND_403:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_self_comment_like_banned, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_RIGHTS_NEED, false);
                    break;
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DELETED_COMMENT_NOT_FOUND, false);
                    break;
                case BAD_REQUEST_ERROR_400:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_also_liked, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;

            }
        }
    };

    private DDScannerRestClient.ResultListener<Void> dislikeCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackCommentLiked();
            isHasNewComment = true;
            reviewsListAdapter.commentDisliked(reviewPositionToRate);
            if (isNeedRefreshComments) {
                getComments();
                isNeedRefreshComments = !isNeedRefreshComments;
            }
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
                    break;
                case RIGHTS_NOT_FOUND_403:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_self_comment_dislike_banned, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_RIGHTS_NEED, false);
                    break;
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DELETED_COMMENT_NOT_FOUND, false);
                    break;
                case BAD_REQUEST_ERROR_400:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_also_disliked, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;

            }
        }
    };

    private DDScannerRestClient.ResultListener<Void> reportCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackDiveSpotReviewReportSent();
            reportType = null;
            reportDescription = null;
            getComments();
            Toast.makeText(ReviewsActivity.this, R.string.report_sent, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
                    break;
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND, false);
                    break;
                case BAD_REQUEST_ERROR_400:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_you_cannot_report_self_review, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<Comments> commentsResultListener = new DDScannerRestClient.ResultListener<Comments>() {
        @Override
        public void onSuccess(Comments result) {
            Comments comments = result;
            ReviewsActivity.this.comments = (ArrayList<Comment>) comments.getComments();
            progressView.setVisibility(View.GONE);
            commentsRc.setVisibility(View.VISIBLE);
            reviewsListAdapter = new ReviewsListAdapter((ArrayList<Comment>) comments.getComments(), ReviewsActivity.this, path);
            commentsRc.setAdapter(reviewsListAdapter);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_dive_spot_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error_title, DialogsRequestCodes.DRC_DIVE_SPOT_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
            }

        }
    };

    private DDScannerRestClient.ResultListener<Void> deleteCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            getComments();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case USER_NOT_FOUND_ERROR_C801:
                    SharedPreferenceHelper.logout();
                    SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT);
                    break;
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DELETED_COMMENT_NOT_FOUND, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Bundle bundle = getIntent().getExtras();
       // comments = (ArrayList<Comment>) bundle.getSerializable("COMMENTS");
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        path = bundle.getString("PATH");
        DDScannerApplication.getDdScannerRestClient().getReportTypes(filtersResponseEntityResultListener);
        findViews();
        toolbarSettings();
        setContent();
        getComments();
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        commentsRc = (RecyclerView) findViewById(R.id.reviews_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        leaveReview = (FloatingActionButton) findViewById(R.id.fab_write_review);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        leaveReview.setOnClickListener(this);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.reviews);
    }

    private void setContent() {
        commentsRc.setHasFixedSize(true);
        commentsRc.setLayoutManager(new LinearLayoutManager(this));
        commentsRc.getItemAnimator().setChangeDuration(0);
     //   commentsRc.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this, path));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW:
                if (resultCode == Activity.RESULT_OK) {
                    getComments();
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_EDIT_MY_REVIEW:
                if (resultCode == RESULT_OK) {
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT:
                if (resultCode == RESULT_OK) {
                    sendReportRequest(reportType, reportDescription);
                }
                if (resultCode == RESULT_CANCELED) {
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT:
                if (resultCode == RESULT_OK) {
                    deleteUsersComment(commentToDelete);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW:
                if (resultCode == RESULT_OK) {
                    likeComment(comments.get(reviewPositionToRate).getId(), reviewPositionToRate);
                    isNeedRefreshComments = true;
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW:
                if (resultCode == RESULT_OK) {
                    dislikeComment(comments.get(reviewPositionToRate).getId(), reviewPositionToRate);
                    isNeedRefreshComments = true;
                }
                break;
        }
    }

    private void getComments() {
        commentsRc.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        isHasNewComment = true;
        DDScannerApplication.getDdScannerRestClient().getCommentsToDiveSpot(diveSpotId, commentsResultListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_write_review:
                LeaveReviewActivity.showForResult(this, diveSpotId, 0f, EventsTracker.SendReviewSource.FROM_REVIEWS_LIST, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW);
                break;
        }
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

    @Subscribe
    public void isCommentLiked(IsCommentLikedEvent event) {
        isHasNewComment = true;
    }

    @Override
    public void onBackPressed() {
        if (isHasNewComment) {
            setResult(RESULT_OK);
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
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

    @Subscribe
    public void showLoginActivity(ShowLoginActivityIntent event) {
        SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN);
    }

    @Subscribe
    public void deleteComment(DeleteCommentEvent event) {
        deleteUsersComment(String.valueOf(event.getCommentId()));
    }

    @Subscribe
    public void editComment(EditCommentEvent editCommentEvent) {
        EditCommentActivity.showForResult(this, editCommentEvent.getComment(), path, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_EDIT_MY_REVIEW);
    }

    private void deleteUsersComment(String id) {
        commentToDelete = id;
        DDScannerApplication.getDdScannerRestClient().deleteUserComment(id, deleteCommentResultListener);
    }

    @Subscribe
    public void showReportDialog(ReportCommentEvent event) {
        reportCommentId = event.getCommentId();
        List<String> objects = new ArrayList<String>();
        for (Map.Entry<String, String> entry : filters.getReport().entrySet()) {
            objects.add(entry.getValue());
        }
        new MaterialDialog.Builder(this)
                .title("Report")
                .items(objects)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                        reportType = Helpers.getMirrorOfHashMap(filters.getReport()).get(text);
                        if (reportType.equals("other")) {
                            showOtherReportDialog();
                            dialog.dismiss();
                        } else {
                            sendReportRequest(reportType, null);
                        }
                    }
                })
                .show();
    }

    private void showOtherReportDialog() {
        new MaterialDialog.Builder(this)
                .title("Other")
                .widgetColor(ContextCompat.getColor(this, R.color.primary))
                .input("Write reason", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().trim().length() > 1) {
                            sendReportRequest("other", input.toString());
                            reportDescription = input.toString();
                        } else {
                            Toast.makeText(ReviewsActivity.this, "Write a reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    private void sendReportRequest(String type, String description) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        DDScannerApplication.getDdScannerRestClient().postSendReportToComment(type, description, reportCommentId, reportCommentResultListener);
    }

    private void likeComment(String id, final int position) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
            return;
        }
        DDScannerApplication.getDdScannerRestClient().postLikeReview(id, likeCommentResultListener);
    }

    private void dislikeComment(String id, final int position) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
            return;
        }
        DDScannerApplication.getDdScannerRestClient().postDislikeReview(id, dislikeCommentResultListener);
    }

    @Subscribe
    public void likeComment(LikeCommentEvent event) {
        this.reviewPositionToRate = event.getPosition();
        likeComment(comments.get(event.getPosition()).getId(), event.getPosition());
    }

    @Subscribe
    public void dislikeComment(DislikeCommentEvent event) {
        this.reviewPositionToRate = event.getPosition();
        dislikeComment(comments.get(event.getPosition()).getId(), event.getPosition());
    }

    @Subscribe
    public void showSliderActivity(ShowSliderForReviewImagesEvent event) {
        ReviewImageSliderActivity.show(this, event.getPhotos(), event.getPosition(), event.isSelfReview(), true, path);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE:
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DIVE_SPOT_NOT_FOUND:
                finish();
                break;
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND:
                getComments();
                break;
        }
    }
}
