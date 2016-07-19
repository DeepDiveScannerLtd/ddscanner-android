package com.ddscanner.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveSpotsListAdapter;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.io.IOException;
import java.util.ArrayList;

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
        findViews();
    }

    private void getAddedDiveSpotList() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersAdded(userId, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
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
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void findViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        rc.setLayoutManager(linearLayoutManager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBar);
        toolbarSettings();
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
