package com.ddscanner.ui.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.Window;

import com.daimajia.swipe.util.Attributes;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.SwipableDiveSpotListAdapter;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 18.5.16.
 */
public class UsersDivespotListSwipableActivity extends AppCompatActivity {

    private RecyclerView rc;
    private Toolbar toolbar;
    private SwipableDiveSpotListAdapter swipableDiveSpotListAdapter;
    private List<DiveSpot> diveSpots = new ArrayList<>();
    private boolean isCheckin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_users_dive_spots);
        isCheckin = getIntent().getBooleanExtra("ISCHECKIN", false);
        findViews();
        if (isCheckin) {
            getListOfDiveSPotsCheckins();
        } else {
            getListOfDiveSPotsFavorites();
        }
    }

    private void findViews() {
        rc = (RecyclerView) findViewById(R.id.divespots_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dive spots");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
    }

    private void setUi() {
        rc.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc.setLayoutManager(linearLayoutManager);

        rc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
            swipableDiveSpotListAdapter = new SwipableDiveSpotListAdapter(this,
                    (ArrayList<DiveSpot>) diveSpots, this, isCheckin);
        swipableDiveSpotListAdapter.setMode(Attributes.Mode.Single);

        rc.setAdapter(swipableDiveSpotListAdapter);
    }

    private void getListOfDiveSPotsCheckins() {
        Map<String, String> map = new HashMap<>();
        if (SharedPreferenceHelper.getIsUserLogined()) {
            map.put("social", SharedPreferenceHelper.getSn());
            map.put("token", SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                map.put("secret",SharedPreferenceHelper.getSecret());
            }
        }
        Call<ResponseBody> call = RestClient.getServiceInstance().getUsersCheckins(
                SharedPreferenceHelper.getUserServerId(), map);
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
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void getListOfDiveSPotsFavorites() {
        Map<String, String> map = new HashMap<>();
        if (SharedPreferenceHelper.getIsUserLogined()) {
            map.put("social", SharedPreferenceHelper.getSn());
            map.put("token", SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                map.put("secret",SharedPreferenceHelper.getSecret());
            }
        }
        Call<ResponseBody> call = RestClient.getServiceInstance().getUsersFavorites(
                SharedPreferenceHelper.getUserServerId(), map);
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
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public static void show(Context context, boolean isCheckins) {
        Intent intent = new Intent(context, UsersDivespotListSwipableActivity.class);
        intent.putExtra("ISCHECKINS", isCheckins);
        context.startActivity(intent);
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
        swipableDiveSpotListAdapter = null;
        finish();
    }
}
