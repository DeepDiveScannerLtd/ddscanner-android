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
import com.ddscanner.entities.ForeignUserDislikesWrapper;
import com.ddscanner.entities.ForeignUserLike;
import com.ddscanner.entities.ForeignUserLikeWrapper;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.rest.BaseCallbackOld;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.ForeignUserLikesAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UserLikesDislikesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private boolean isLikes;
    private String userId;
    private ProgressView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_user_likes_dislikes);
        isLikes = getIntent().getBooleanExtra(Constants.USER_LIKES_ACTIVITY_INTENT_IS_LIKE, false);
        userId = getIntent().getStringExtra(Constants.USER_LIKES_ACTIVITY_INTENT_USER_ID);
        findViews();
        if (isLikes) {
            getUserLikes();
        } else {
            getUserDislikes();
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

    private void getUserDislikes() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserDislikes(userId, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        ForeignUserDislikesWrapper foreignUserDislikesWrapper = new Gson().fromJson(responseString, ForeignUserDislikesWrapper.class);
                        recyclerView.setAdapter(new ForeignUserLikesAdapter(UserLikesDislikesActivity.this, (ArrayList<ForeignUserLike>) foreignUserDislikesWrapper.getDislikes(), false));
                        recyclerView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    } catch (IOException e) {

                    }
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
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SocialNetworks.showForResult(UserLikesDislikesActivity.this, ActivitiesRequestCodes.REQUEST_CODE_USER_LIKES_DISLIKES_ACTIVITY_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(UserLikesDislikesActivity.this);
            }
        });
    }

    private void getUserLikes() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserLikes(userId, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        ForeignUserLikeWrapper foreignUserLikeWrapper = new Gson().fromJson(responseString, ForeignUserLikeWrapper.class);
                        recyclerView.setAdapter(new ForeignUserLikesAdapter(UserLikesDislikesActivity.this, (ArrayList<ForeignUserLike>) foreignUserLikeWrapper.getLikes(), true));
                        recyclerView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    } catch (IOException e) {

                    }
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
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SocialNetworks.showForResult(UserLikesDislikesActivity.this, ActivitiesRequestCodes.REQUEST_CODE_USER_LIKES_DISLIKES_ACTIVITY_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(UserLikesDislikesActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(UserLikesDislikesActivity.this);
            }
        });
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
                        getUserLikes();
                    } else {
                        getUserDislikes();
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

}
