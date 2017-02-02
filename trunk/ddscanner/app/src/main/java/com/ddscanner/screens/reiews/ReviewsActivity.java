package com.ddscanner.screens.reiews;

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
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
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
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.screens.reiews.edit.EditCommentActivity;
import com.ddscanner.screens.reiews.add.LeaveReviewActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener, DialogClosedListener {

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
    private ReviewsListAdapter reviewsListAdapter;
    private int reviewPositionToRate;
    private boolean isNeedRefresh;
    private int commentPosition;
    private MaterialDialog materialDialog;

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
            reviewsListAdapter.rateReviewFaled(reviewPositionToRate);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            reviewsListAdapter.rateReviewFaled(reviewPositionToRate);
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;

            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
            reviewsListAdapter.rateReviewFaled(reviewPositionToRate);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            reviewsListAdapter.rateReviewFaled(reviewPositionToRate);
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
                    break;
                default:
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;

            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
                    break;
                default:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    private DDScannerRestClient.ResultListener<ArrayList<CommentEntity>> commentsResultListener = new DDScannerRestClient.ResultListener<ArrayList<CommentEntity>>() {
        @Override
        public void onSuccess(ArrayList<CommentEntity> result) {
            ReviewsActivity.this.comments = result;
            progressView.setVisibility(View.GONE);
            commentsRecyclerView.setVisibility(View.VISIBLE);
            ArrayList<CommentEntity> commentsList = new ArrayList<>();
            commentsList = result;
            reviewsListAdapter = new ReviewsListAdapter(commentsList, ReviewsActivity.this);
            commentsRecyclerView.setAdapter(reviewsListAdapter);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

    };

    private DDScannerRestClient.ResultListener<Void> deleteCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            reviewsListAdapter.deleteComment(commentToDelete);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT);
                    break;
                default:
                    InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_server_error_title, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 0) {
            leaveReview.setVisibility(View.GONE);
        }
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
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_EDIT_MY_REVIEW:
                if (resultCode == RESULT_OK) {
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT:
//                if (resultCode == RESULT_OK) {
//                    sendReportRequest(reportType, reportDescription);
//                }
//                if (resultCode == RESULT_CANCELED) {
//                    getComments();
//                }
                if (requestCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT:
                if (resultCode == RESULT_OK) {
//                    deleteUsersComment(commentToDelete);
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW:
                if (resultCode == RESULT_OK) {
//                    likeComment(comments.get(reviewPositionToRate).getComment().getId(), reviewPositionToRate);
//                    isNeedRefresh = true;
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW:
                if (resultCode == RESULT_OK) {
//                    dislikeComment(comments.get(reviewPositionToRate).getComment().getId(), reviewPositionToRate);
//                    isNeedRefresh = true;
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    getComments();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_SHOW_SLIDER:
                if (resultCode == RESULT_OK) {
                    if (data.getSerializableExtra("deletedImages") != null) {
                        reviewsListAdapter.imageDeleted(commentPosition, (ArrayList<DiveSpotPhoto>) data.getSerializableExtra("deletedImages"));
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REVIEW:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsMustRefreshDiveSpotActivity(true);
                    if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == 0) {
                        UserActionInfoDialogFragment.show(this, R.string.sorry, R.string.dive_centers_cannot_leave_review);
                        leaveReview.setVisibility(View.GONE);
                    } else {
                        LeaveReviewActivity.showForResult(this, diveSpotId, 1, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW);
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
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                    LeaveReviewActivity.showForResult(this, diveSpotId, 1, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW);
                } else {
                    LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REVIEW);
                }
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
        Intent intent = new Intent();
        if (reviewsListAdapter != null) {
            intent.putExtra("count", String.valueOf(reviewsListAdapter.getItemCount()));
        }
        setResult(RESULT_OK, intent);
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
        materialDialog.show();
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
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postReportReview(reportCommentResultListener, new ReportRequest(String.valueOf(reportType), description, reportCommentId));
    }

    private void likeComment(String id, final int position) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postLikeReview(id, likeCommentResultListener);
        reviewsListAdapter.rateReviewRequestStarted(position);
    }

    private void dislikeComment(String id, final int position) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
            return;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().postDislikeReview(id, dislikeCommentResultListener);
        reviewsListAdapter.rateReviewRequestStarted(position);
    }

    @Subscribe
    public void likeComment(LikeCommentEvent event) {
        this.reviewPositionToRate = event.getPosition();
        comments = reviewsListAdapter.getCommentsList();
        likeComment(comments.get(event.getPosition()).getComment().getId(), event.getPosition());
    }

    @Subscribe
    public void dislikeComment(DislikeCommentEvent event) {
        this.reviewPositionToRate = event.getPosition();
        comments = reviewsListAdapter.getCommentsList();
        dislikeComment(comments.get(event.getPosition()).getComment().getId(), event.getPosition());
    }

    @Subscribe
    public void showSliderActivity(ShowSliderForReviewImagesEvent event) {
        this.commentPosition = event.getCommentPosition();
        DDScannerApplication.getInstance().getDiveSpotPhotosContainer().setPhotos(event.getPhotos());
        ImageSliderActivity.showForResult(this, event.getPhotos(), event.getPosition(), ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_SHOW_SLIDER, PhotoOpenedSource.REVIEW, comments.get(commentPosition).getComment().getId());
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE:
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_DIVE_SPOT_NOT_FOUND:
            case DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_REPORTED_COMMENT_NOT_FOUND:
                finish();
                break;
        }
    }
}
