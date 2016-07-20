package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ddscanner.R;
import com.ddscanner.entities.ForeignUserLikeWrapper;
import com.ddscanner.rest.RestClient;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 19.7.16.
 */
public class ForeignUserLikesDislikesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private boolean isLikes;
    private String userId;
    private ProgressView progressView;
    private Helpers helpers = new Helpers();

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
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserDislikes(userId, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        recyclerView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getUserLikes() {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserDislikes(userId, helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        ForeignUserLikeWrapper foreignUserLikeWrapper = new Gson().fromJson(responseString, ForeignUserLikeWrapper.class);
                        recyclerView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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



    public static void show(Activity context, boolean isLikes, String userId) {
        Intent intent = new Intent(context, ForeignUserLikesDislikesActivity.class);
        intent.putExtra(Constants.USER_LIKES_ACTIVITY_INTENT_IS_LIKE, isLikes);
        intent.putExtra(Constants.USER_LIKES_ACTIVITY_INTENT_USER_ID, userId);
        context.startActivity(intent);
    }

}
