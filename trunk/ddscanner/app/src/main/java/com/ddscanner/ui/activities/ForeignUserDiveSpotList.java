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
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
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
import com.ddscanner.ui.adapters.DiveSpotsListAdapter;
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

public class ForeignUserDiveSpotList extends AppCompatActivity {

    private RecyclerView rc;
    private Toolbar toolbar;
    private ProgressView progressBarFull;
    private String toolbarTitle;
    private String userId;
    private boolean isEdited;
    private boolean isCreated;
    private boolean isCheckIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
        userId = getIntent().getStringExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_USER_ID);
        isEdited = getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISEDITED, false);
        isCreated = getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCREATED, false);
        isCheckIn = getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCHECKIN, false);
        findViews();
        if (isEdited) {
            toolbarTitle = getString(R.string.edited);
            getEditedDiveSpotList();
        }
        if (isCreated) {
            toolbarTitle = getString(R.string.created);
            getAddedDiveSpotList();
        }
        if (isCheckIn) {
            toolbarTitle = getString(R.string.toolbar_title_check_ins);
            getUsersCheckinList();
        }
        toolbarSettings();
    }

    private void getAddedDiveSpotList() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersAdded(userId, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                        progressBarFull.setVisibility(View.GONE);
                        rc.setVisibility(View.VISIBLE);
                        rc.setAdapter(new DiveSpotsListAdapter((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), ForeignUserDiveSpotList.this, EventsTracker.SpotViewSource.FROM_PROFILE_CREATED));
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
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SocialNetworks.showForResult(ForeignUserDiveSpotList.this, ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ForeignUserDiveSpotList.this);
            }
        });
    }

    private void getEditedDiveSpotList() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersEdited(userId, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                        progressBarFull.setVisibility(View.GONE);
                        rc.setVisibility(View.VISIBLE);
                        rc.setAdapter(new DiveSpotsListAdapter((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), ForeignUserDiveSpotList.this, EventsTracker.SpotViewSource.FROM_PROFILE_EDITED));
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
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SocialNetworks.showForResult(ForeignUserDiveSpotList.this, ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ForeignUserDiveSpotList.this);
            }
        });
    }

    private void getUsersCheckinList() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersCheckins(userId, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                        progressBarFull.setVisibility(View.GONE);
                        rc.setVisibility(View.VISIBLE);
                        rc.setAdapter(new DiveSpotsListAdapter((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), ForeignUserDiveSpotList.this, EventsTracker.SpotViewSource.FROM_PROFILE_CHECKINS));
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
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SocialNetworks.showForResult(ForeignUserDiveSpotList.this, ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(ForeignUserDiveSpotList.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(ForeignUserDiveSpotList.this);
            }
        });
    }

    private void findViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        rc.setLayoutManager(linearLayoutManager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBar);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void show(Activity context, boolean isEdited, boolean isCreated, boolean isCheckin, String userId) {
        Intent intent = new Intent(context, ForeignUserDiveSpotList.class);
        intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_USER_ID, userId);
        if (isEdited) {
            intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISEDITED, true);
        }
        if (isCreated) {
            intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCREATED, true);
        }
        if (isCheckin) {
            intent.putExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCHECKIN, true);
        }
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_FOREIGN_USER_SPOT_LIST_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isCheckIn) {
                        getUsersCheckinList();
                    }
                    if (isEdited) {
                        getEditedDiveSpotList();
                    }
                    if (isCreated) {
                        getAddedDiveSpotList();
                    }
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
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

}
