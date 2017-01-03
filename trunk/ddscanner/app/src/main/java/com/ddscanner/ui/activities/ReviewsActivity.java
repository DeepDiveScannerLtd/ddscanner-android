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
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.DislikeCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.IsCommentLikedEvent;
import com.ddscanner.events.LikeCommentEvent;
import com.ddscanner.events.ReportCommentEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.ShowSliderForReviewImagesEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private ArrayList<CommentEntity> comments;
    private RecyclerView commentsRecyclerView;
    private ProgressView progressView;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private String diveSpotId;
    private String commentToDelete;

    private boolean isHasNewComment = false;

    private List<String> reportItems = new ArrayList<>();

    private String reportCommentId;
    private String reportType;
    private String reportDescription = null;
    private MaterialDialog materialDialog;
    private ReviewsListAdapter reviewsListAdapter;
    private int reviewPositionToRate;
    private boolean isNeedRefresh;
    private int commentPosition;

    private DDScannerRestClient.ResultListener<Void> likeCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackCommentLiked();
            reviewsListAdapter.commentLiked(reviewPositionToRate);
            isHasNewComment = true;
            if (isNeedRefresh) {
                getComments();
                isNeedRefresh = !isNeedRefresh;
            }
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
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
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_server_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
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
            if (isNeedRefresh) {
                getComments();
                isNeedRefresh = !isNeedRefresh;
            }
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
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
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_server_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
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
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
                    break;
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND, false);
                    break;
                case BAD_REQUEST_ERROR_400:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_you_cannot_report_self_review, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND, false);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_server_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<ArrayList<CommentEntity>> commentsResultListener = new DDScannerRestClient.ResultListener<ArrayList<CommentEntity>>() {
        @Override
        public void onSuccess(ArrayList<CommentEntity> result) {
            ReviewsActivity.this.comments = result;
            progressView.setVisibility(View.GONE);
            commentsRecyclerView.setVisibility(View.VISIBLE);
            ArrayList<CommentEntity> List = new ArrayList<>();
           List = result;
            reviewsListAdapter = new ReviewsListAdapter(List, ReviewsActivity.this);
            commentsRecyclerView.setAdapter(reviewsListAdapter);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_dive_spot_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_server_error_title, DialogsRequestCodes.DRC_DIVE_SPOT_PHOTOS_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
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
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT);
                    break;
                case COMMENT_NOT_FOUND_ERROR_C803:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_comment_not_found, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DELETED_COMMENT_NOT_FOUND, false);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_server_error_title, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;
            }
        }
    };

    public static void showForResult(Activity context, String diveSpotId, int requestCode) {
        Intent intent = new Intent(context, ReviewsActivity.class);
        intent.putExtra(Constants.DIVESPOTID, diveSpotId);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Bundle bundle = getIntent().getExtras();
       // comments = (ArrayList<CommentEntity>) bundle.getSerializable("");
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        findViews();
        toolbarSettings();
        setContent();
        getComments();
    }

    private void findViews() {
        materialDialog = Helpers.getMaterialDialog(this);
        commentsRecyclerView = (RecyclerView) findViewById(R.id.reviews_rc);
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
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.getItemAnimator().setChangeDuration(0);
     //   commentsRecyclerView.setAdapter(new ReviewsListAdapter(comments, ReviewsActivity.this, path));
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
                    likeComment(comments.get(reviewPositionToRate).getComment().getId(), reviewPositionToRate);
                    isNeedRefresh = true;
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW:
                if (resultCode == RESULT_OK) {
                    dislikeComment(comments.get(reviewPositionToRate).getComment().getId(), reviewPositionToRate);
                    isNeedRefresh = true;
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_SHOW_SLIDER:
                if (resultCode == RESULT_OK) {
                    if (data.getSerializableExtra("deletedImages") != null) {
                        reviewsListAdapter.imageDeleted(commentPosition, (ArrayList<String>) data.getSerializableExtra("deletedImages"));
                    }
                }
                break;
        }
    }

    private void getComments() {
        commentsRecyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        isHasNewComment = true;
        DDScannerApplication.getInstance().getDdScannerRestClient().getCommentsForDiveSpot(commentsResultListener, diveSpotId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_write_review:
                LeaveReviewActivity.showForResult(this, diveSpotId, 0f, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW);
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
        LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN);
    }

    @Subscribe
    public void deleteComment(DeleteCommentEvent event) {
        deleteUsersComment(String.valueOf(event.getCommentId()));
    }

    @Subscribe
    public void editComment(EditCommentEvent editCommentEvent) {
        EditCommentActivity.showForResult(this, editCommentEvent.getComment(), ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_EDIT_MY_REVIEW);
    }

    private void deleteUsersComment(String id) {
        commentToDelete = id;
        DDScannerApplication.getInstance().getDdScannerRestClient().postDeleteReview(deleteCommentResultListener, id);
    }

    @Subscribe
    public void showReportDialog(ReportCommentEvent event) {
        reportCommentId = event.getCommentId();
        List<String> objects = Helpers.getReportTypes();
        new MaterialDialog.Builder(this)
                .title("Report")
                .items(objects)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                        reportType = String.valueOf(Helpers.getReportTypes().indexOf(text) + 1);
                        if (text.equals("Other")) {
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
                            sendReportRequest(reportType, input.toString());
                            reportDescription = input.toString();
                        } else {
                            Toast.makeText(ReviewsActivity.this, "Write a reason", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    private void sendReportRequest(String type, String description) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postReportReview(reportCommentResultListener, new ReportRequest(String.valueOf(reportType), description, reportCommentId));
    }

    private void likeComment(String id, final int position) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postLikeReview(id, likeCommentResultListener);
    }

    private void dislikeComment(String id, final int position) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postDislikeReview(id, dislikeCommentResultListener);
    }

    @Subscribe
    public void likeComment(LikeCommentEvent event) {
        this.reviewPositionToRate = event.getPosition();
        likeComment(comments.get(event.getPosition()).getComment().getId(), event.getPosition());
    }

    @Subscribe
    public void dislikeComment(DislikeCommentEvent event) {
        this.reviewPositionToRate = event.getPosition();
        dislikeComment(comments.get(event.getPosition()).getComment().getId(), event.getPosition());
    }

    @Subscribe
    public void showSliderActivity(ShowSliderForReviewImagesEvent event) {
        this.commentPosition = event.getCommentPosition();
    //    ReviewImageSliderActivity.showForResult(this, event.getPhotos(), event.getPosition(), event.isSelfReview(), true, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_SHOW_SLIDER);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE:
                finish();
                break;
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DIVE_SPOT_NOT_FOUND:
                finish();
                break;
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND:
                getComments();
                break;
        }
    }
}
