package com.ddscanner.screens.reiews.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.PhotoOpenedSource;
import com.ddscanner.entities.ReviewsOpenedSource;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.DislikeCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.IsCommentLikedEvent;
import com.ddscanner.events.LikeCommentEvent;
import com.ddscanner.events.ReportCommentEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.events.ShowSliderForReviewImagesEvent;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.photo.slider.ImageSliderActivity;
import com.ddscanner.screens.reiews.add.LeaveReviewActivity;
import com.ddscanner.screens.reiews.edit.EditCommentActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends BaseAppCompatActivity implements View.OnClickListener, DialogClosedListener {

    private static final String ARG_OPENED_SOURCE = "isuser";
    private static final String ARG_LOCATION = "location";

    private ArrayList<CommentEntity> comments;
    private RecyclerView commentsRecyclerView;
    private ProgressView progressView;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private String sourceId;
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
    private int reportReviewPosition;
    private ReviewsOpenedSource openedSource;
    private LatLng diveSpotLocation;

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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            reviewsListAdapter.rateReviewFaled(reviewPositionToRate);
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
                    break;
                default:
                    UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;

            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    private DDScannerRestClient.ResultListener<Void> dislikeCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackCommentDisliked();
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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            reviewsListAdapter.rateReviewFaled(reviewPositionToRate);
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
                    break;
                default:
                    UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_FAILED_TO_CONNECT, false);
                    break;

            }
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    private DDScannerRestClient.ResultListener<Void> reportCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackDiveSpotReviewReportSent();
            reportType = null;
            reportDescription = null;
            materialDialog.dismiss();
            reviewsListAdapter.deleteComment(reportCommentId);
            Toast.makeText(ReviewsActivity.this, R.string.report_sent, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
                    break;
                default:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
            switch (openedSource) {
                case DIVESPOT:
                    reviewsListAdapter = new ReviewsListAdapter(commentsList, ReviewsActivity.this, null, DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
                    break;
                case USER:
                case SINGLE:
                    reviewsListAdapter = new ReviewsListAdapter(commentsList, ReviewsActivity.this, sourceId, DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
                    break;
            }
            commentsRecyclerView.setAdapter(reviewsListAdapter);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_REVIEWS_ACTIVITY_CONNECTION_FAILURE, false);
        }

    };

    private DDScannerRestClient.ResultListener<Void> deleteCommentResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackReviewDeleted();
            materialDialog.dismiss();
            reviewsListAdapter.deleteComment(commentToDelete);
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logoutFromAllAccounts();
                    LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT);
                    break;
                default:
                    UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.unexcepted_error_title, R.string.unexcepted_error_text, false);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    public static void showForResult(Activity context, String diveSpotId, int requestCode, ReviewsOpenedSource isUserReviews) {
        Intent intent = new Intent(context, ReviewsActivity.class);
        intent.putExtra(Constants.DIVESPOTID, diveSpotId);
        intent.putExtra(ARG_OPENED_SOURCE, isUserReviews);
        context.startActivityForResult(intent, requestCode);
    }

    public static void showForDiveSpot(Activity context, String diveSpotId, int requestCode, ReviewsOpenedSource isUserReviews, LatLng latLng) {
        Intent intent = new Intent(context, ReviewsActivity.class);
        intent.putExtra(Constants.DIVESPOTID, diveSpotId);
        intent.putExtra(ARG_OPENED_SOURCE, isUserReviews);
        intent.putExtra(ARG_LOCATION, latLng);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Bundle bundle = getIntent().getExtras();
        sourceId = bundle.getString(Constants.DIVESPOTID);
        openedSource = (ReviewsOpenedSource) bundle.getSerializable(ARG_OPENED_SOURCE);
        switch (openedSource) {
            case USER:

                break;
            case DIVESPOT:
                EventsTracker.trackDeviSpotReviewsView();
                diveSpotLocation = getIntent().getParcelableExtra(ARG_LOCATION);
                break;
        }
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
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER || !openedSource.equals(ReviewsOpenedSource.DIVESPOT)) {
            leaveReview.setVisibility(View.GONE);
            leaveReview.setOnClickListener(null);
        } else {
            leaveReview.setVisibility(View.VISIBLE);
            leaveReview.setOnClickListener(this);
        }
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (openedSource.equals(ReviewsOpenedSource.SINGLE)) {
            getSupportActionBar().setTitle(R.string.single_reiew_title);
        } else {
            getSupportActionBar().setTitle(R.string.reviews);
        }
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
                    if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
                        UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.sorry, R.string.dive_centers_cannot_leave_review, false);
                        leaveReview.setVisibility(View.GONE);
                    } else {
                        LeaveReviewActivity.showForResult(this, sourceId, 1, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW, diveSpotLocation);
                    }
                }
                break;
        }
    }

    private void getComments() {
        commentsRecyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        isHasNewComment = true;
        switch (openedSource) {
            case USER:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getUsersComments(sourceId, commentsResultListener);
                break;
            case DIVESPOT:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getCommentsForDiveSpot(commentsResultListener, sourceId);
                break;
            case SINGLE:
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getSingleReview(sourceId, commentsResultListener);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_write_review:
                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                    LeaveReviewActivity.showForResult(this, sourceId, 1, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_WRITE_REVIEW, diveSpotLocation);
                } else {
                    LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REVIEW);
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
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
        commentsResultListener.setCancelled(true);
        likeCommentResultListener.setCancelled(true);
        dislikeCommentResultListener.setCancelled(true);
        reportCommentResultListener.setCancelled(true);
        deleteCommentResultListener.setCancelled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        commentsResultListener.setCancelled(false);
        likeCommentResultListener.setCancelled(false);
        dislikeCommentResultListener.setCancelled(false);
        reportCommentResultListener.setCancelled(false);
        deleteCommentResultListener.setCancelled(false);
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
        EventsTracker.trackEditReview();
        EditCommentActivity.showForResult(this, editCommentEvent.getComment(), ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_EDIT_MY_REVIEW, editCommentEvent.isHaveSealife());
    }

    private void deleteUsersComment(String id) {
        EventsTracker.trackDeleteReview();
        materialDialog.show();
        commentToDelete = id;
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postDeleteReview(deleteCommentResultListener, id);
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
        EventsTracker.trackReviewReport();
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            LoginActivity.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        materialDialog.show();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postReportReview(reportCommentResultListener, new ReportRequest(String.valueOf(reportType), description, reportCommentId));
    }

    private void likeComment(String id, final int position) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
            return;
        }
        reviewsListAdapter.rateReviewRequestStarted(position);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postLikeReview(id, likeCommentResultListener);
    }

    private void dislikeComment(String id, final int position) {
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
            LoginActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
            return;
        }
        reviewsListAdapter.rateReviewRequestStarted(position);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postDislikeReview(id, dislikeCommentResultListener);
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
