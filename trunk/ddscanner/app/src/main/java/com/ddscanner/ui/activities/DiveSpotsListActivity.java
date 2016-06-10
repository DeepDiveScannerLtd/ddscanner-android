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

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
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
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.ProductListAdapter;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 20.5.16.
 */
public class DiveSpotsListActivity extends AppCompatActivity {

    private static final int RC_LOGIN = 9001;
    
    private RecyclerView rc;
    private Toolbar toolbar;
    private List<DiveSpot> diveSpots = new ArrayList<>();
    private boolean isAdded = false;
    private Helpers helpers = new Helpers();
    private ProgressView progressBarFull;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dive_spots);
        isAdded = getIntent().getBooleanExtra("ISADDED", false);
        if (isAdded) {
            getAddedList();
        } else {
            getEditedList();
        }
        findViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN) {
            if (resultCode == RESULT_OK) {
                if (isAdded) {
                    getAddedList();
                } else {
                    getEditedList();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
    
    private void findViews() {
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBarFull = (ProgressView) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (isAdded) {
            getSupportActionBar().setTitle(R.string.added_ds);
        } else {
            getSupportActionBar().setTitle(R.string.edited_ds);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    private void setUi() {
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);
        rc.setAdapter(new ProductListAdapter((ArrayList<DiveSpot>) diveSpots, this));
        progressBarFull.setVisibility(View.GONE);
        rc.setVisibility(View.VISIBLE);
    }

    private void getAddedList() {
        Call<ResponseBody> call = RestClient.getServiceInstance().getUsersAdded(
                SharedPreferenceHelper.getUserServerId(), helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    DivespotsWrapper divespotsWrapper = new Gson()
                            .fromJson(responseString, DivespotsWrapper.class);
                    diveSpots = divespotsWrapper.getDiveSpots();
                    setUi();
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
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(DiveSpotsListActivity.this, RC_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getEditedList() {
        Call<ResponseBody> call = RestClient.getServiceInstance().getUsersEdited(
                SharedPreferenceHelper.getUserServerId(), helpers.getUserQuryMapRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    DivespotsWrapper divespotsWrapper = new Gson()
                            .fromJson(responseString, DivespotsWrapper.class);
                    diveSpots = divespotsWrapper.getDiveSpots();
                    setUi();
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
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(DiveSpotsListActivity.this, RC_LOGIN);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        helpers.showToast(DiveSpotsListActivity.this, R.string.toast_server_error);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static void show(Context context, boolean isAdded) {
        Intent intent = new Intent(context, DiveSpotsListActivity.class);
        intent.putExtra("ISADDED", isAdded);
        context.startActivity(intent);
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


