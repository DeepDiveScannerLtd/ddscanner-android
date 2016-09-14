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
import com.ddscanner.entities.Comment;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.DeleteCommentEvent;
import com.ddscanner.events.EditCommentEvent;
import com.ddscanner.events.ShowLoginActivityIntent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.SelfReviewsListAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfCommentsActivity extends AppCompatActivity {

    private ArrayList<Comment> comments;
    private RecyclerView commentsRc;
    private ProgressView progressView;

    private Toolbar toolbar;
    private FloatingActionButton leaveReview;

    private Helpers helpers = new Helpers();
    private String commentToDelete;
    private String userId;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        userId = getIntent().getStringExtra(Constants.SELF_REVIEWS_ACTIVITY_INTENT_USER_ID);
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
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserComments(userId, helpers.getUserQuryMapRequest());
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
                    progressView.setVisibility(View.GONE);
                    commentsRc.setVisibility(View.VISIBLE);
                    path = comments.getDiveSpotPathMedium();
                    commentsRc.setAdapter(new SelfReviewsListAdapter((ArrayList<Comment>) comments.getComments(), SelfCommentsActivity.this, comments.getDiveSpotPathMedium()));
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
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(SelfCommentsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_VIEW_COMMENTS);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(SelfCommentsActivity.this);
            }
        });
    }

    public static void show(Context context, String userId) {
        Intent intent = new Intent(context, SelfCommentsActivity.class);
        intent.putExtra(Constants.SELF_REVIEWS_ACTIVITY_INTENT_USER_ID, userId);
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
                if (resultCode == RESULT_CANCELED && !SharedPreferenceHelper.isUserLoggedIn()) {
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
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteComment(id, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    getComments();
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
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(SelfCommentsActivity.this, ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_LOGIN_TO_DELETE_COMMENTS);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(SelfCommentsActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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
        EditCommentActivity.showForResult(this, editCommentEvent.getComment(), path, ActivitiesRequestCodes.REQUEST_CODE_SELF_REVIEWS_EDIT_MY_REVIEW);
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
        if (!helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

}
