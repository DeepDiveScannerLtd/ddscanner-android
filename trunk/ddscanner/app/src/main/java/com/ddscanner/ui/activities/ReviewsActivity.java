package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.ddscanner.events.ShowUserDialogEvent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.ReviewsListAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
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

public class ReviewsActivity extends AppCompatActivity implements View.OnClickListener {

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
    private boolean isClickedReport;
    private int reviewPositionToRate;
    private boolean isNeedRefreshComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Bundle bundle = getIntent().getExtras();
       // comments = (ArrayList<Comment>) bundle.getSerializable("COMMENTS");
        diveSpotId = bundle.getString(Constants.DIVESPOTID);
        path = bundle.getString("PATH");
        getReportsTypes();
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
                    isHasNewComment = true;
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
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getComments(diveSpotId, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Comments comments = new Gson().fromJson(responseString, Comments.class);
                    ReviewsActivity.this.comments = (ArrayList<Comment>) comments.getComments();
                    progressView.setVisibility(View.GONE);
                    commentsRc.setVisibility(View.VISIBLE);
                    reviewsListAdapter = new ReviewsListAdapter((ArrayList<Comment>) comments.getComments(), ReviewsActivity.this, path);
                    commentsRc.setAdapter(reviewsListAdapter);
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ReviewsActivity.this);
            }
        });
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
    public void showDialog(ShowUserDialogEvent event) {
        Helpers.showDialog(event.getUser(), getSupportFragmentManager());
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
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteComment(id, Helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    getComments();
                    isHasNewComment = true;
                } else {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DELETE_COMMENT);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getReportsTypes() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {

                    }
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(responseString).getAsJsonObject();
                    JsonObject currentsJsonObject = jsonObject.getAsJsonObject(Constants.FILTERS_VALUE_REPORT);
                    for (Map.Entry<String, JsonElement> elementEntry : currentsJsonObject.entrySet()) {
                        filters.getReport().put(elementEntry.getKey(), elementEntry.getValue().getAsString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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

                        isClickedReport = true;
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
                .widgetColor(getResources().getColor(R.color.primary))
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
        materialDialog.show();
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        ReportRequest reportRequest = new ReportRequest();
        if (description != null) {
            reportRequest.setDescription(description);
        }
        if (!SharedPreferenceHelper.getToken().isEmpty()) {
            reportRequest.setToken(SharedPreferenceHelper.getToken());
            reportRequest.setSocial(SharedPreferenceHelper.getSn());
        } else {
            SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
            return;
        }
        reportRequest.setType(type);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportComment(reportCommentId, reportRequest);
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                materialDialog.dismiss();
                if (response.isSuccessful()) {
                    EventsTracker.trackDiveSpotReviewReportSent();
                    isClickedReport = false;
                    reportType = null;
                    reportDescription = null;
                    getComments();
                    isHasNewComment = true;
                    Toast.makeText(ReviewsActivity.this, R.string.report_sent, Toast.LENGTH_SHORT).show();
                } else {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LEAVE_REPORT);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ReviewsActivity.this);
            }
        });
    }

    private void likeComment(String id, final int position) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().likeComment(
                id, Helpers.getRegisterRequest()
        );
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.raw().code() == 200) {
                        EventsTracker.trackCommentLiked();
                        reviewsListAdapter.commentLiked(position);
                        isHasNewComment = true;
                        if (isNeedRefreshComments) {
                            getComments();
                            isNeedRefreshComments = !isNeedRefreshComments;
                        }
                    }
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.raw().code() == 403) {
                        Gson gson = new Gson();
                        GeneralError generalError;
                        generalError = gson.fromJson(responseString, GeneralError.class);
                        Toast toast = Toast.makeText(ReviewsActivity.this, R.string.yoy_cannot_like_review, Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_LIKE_REVIEW);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ReviewsActivity.this);
            }
        });
    }

    private void dislikeComment(String id, final int position) {
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            SocialNetworks.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().dislikeComment(
                id, Helpers.getRegisterRequest()
        );
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.raw().code() == 200) {
                        EventsTracker.trackCommentLiked();
                        isHasNewComment = true;
                        reviewsListAdapter.commentDisliked(position);
                        if (isNeedRefreshComments) {
                            getComments();
                            isNeedRefreshComments = !isNeedRefreshComments;
                        }
                    }
                }
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.raw().code() == 403) {
                        Gson gson = new Gson();
                        GeneralError generalError;
                        generalError = gson.fromJson(responseString, GeneralError.class);
                        Toast toast = Toast.makeText(ReviewsActivity.this, R.string.yoy_cannot_like_review, Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(ReviewsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_REVIEWS_ACTIVITY_LOGIN_TO_DISLIKE_REVIEW);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ReviewsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ReviewsActivity.this);
            }
        });
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
}
