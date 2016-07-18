package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.ddscanner.R;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.rey.material.widget.ProgressView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 18.7.16.
 */
public class ForeignUserDiveSpotList extends AppCompatActivity {

    private RecyclerView rc;
    private Toolbar toolbar;
    private ProgressView progressBarFull;
    private String toolbarTitle;
    private String userId;
    private Helpers helpers = new Helpers();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
        userId = getIntent().getStringExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_USER_ID);
        if (getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISEDITED, false)) {
            toolbarTitle = getString(R.string.edited);
            getEditedDiveSpotList();
        }
        if (getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCREATED, false)) {
            toolbarTitle = getString(R.string.created);
            getAddedDiveSpotList();
        }
        if (getIntent().getBooleanExtra(Constants.FOREIGN_USER_ACTIVITY_INTENT_ISCHECKIN, false)) {
            toolbarTitle = getString(R.string.check_in);
            getUsersCheckinList();
        }
        findViews();
    }

    private void getAddedDiveSpotList() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersAdded(userId, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getEditedDiveSpotList() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersEdited(userId, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getUsersCheckinList() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersCheckins(userId, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void findViews() {
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBar);
    }

    public static void show(Context context, boolean isEdited, boolean isCreated, boolean isCheckin, String userId) {
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
}
