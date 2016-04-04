package com.ddscanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveCentersPagerAdapter;
import com.ddscanner.utils.EventTrackerHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by lashket on 29.1.16.
 */
public class DiveCentersActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "DiveCentersActivity";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DiveCentersResponseEntity divecenters = new DiveCentersResponseEntity();
    private DiveCentersPagerAdapter diveCentersPagerAdapter;
    private LatLng latLng;
    private Map<String, String> map = new HashMap<>();
    private ProgressDialog progressDialog;
    private String dsName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_centers);
        findViews();
        latLng = getIntent().getParcelableExtra("LATLNG");
        dsName = getIntent().getStringExtra("NAME");
        populateDiveCentesPager();
       // requestDiveCenters(latLng);
    }

    private void findViews() {
        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.place_view_pager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dive centers");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
    }

    private void populateDiveCentesPager() {
        diveCentersPagerAdapter = new DiveCentersPagerAdapter(this, getFragmentManager(), latLng, dsName);
        viewPager.setAdapter(diveCentersPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void show(Context context, LatLng latLng, String name) {
        Intent intent = new Intent(context, DiveCentersActivity.class);
        intent.putExtra("LATLNG", latLng);
        intent.putExtra("NAME", name);
        context.startActivity(intent);
    }

    private void requestDiveCenters(LatLng latLng) {
        progressDialog = new ProgressDialog(DiveCentersActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.pleaseWait));
        progressDialog.show();
        map.put("latLeft", String.valueOf(latLng.latitude - 2.0));
        map.put("lngLeft", String.valueOf(latLng.longitude - 2.0));
        map.put("lngRight", String.valueOf(latLng.longitude + 2.0));
        map.put("latRight", String.valueOf(latLng.latitude + 2.0));
        RestClient.getServiceInstance().getDiveCenters(map, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                System.out.println(responseString);
                divecenters = new Gson().fromJson(responseString, DiveCentersResponseEntity.class);
                populateDiveCentesPager();
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    Toast.makeText(DiveCentersActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(DiveCentersActivity.this, "Server is not responsible, please try later", Toast.LENGTH_LONG).show();
                }
                if (error != null) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    Log.i(TAG, json);
                }
            }
        });
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
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_DIVE_CENTERS_MAP_OPENED, new HashMap<String, Object>());
                break;
            case 1:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext().getApplicationContext(),
                        EventTrackerHelper.EVENT_DIVE_CENTERS_LIST_OPENED, new HashMap<String, Object>());
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "Starting");

    }
}
