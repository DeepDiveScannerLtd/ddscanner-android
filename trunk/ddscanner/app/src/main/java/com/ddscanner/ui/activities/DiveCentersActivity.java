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
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.DiveCentersPagerAdapter;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.Helpers;
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
    private DiveCentersPagerAdapter diveCentersPagerAdapter;
    private LatLng latLng;
    private String dsName;
    private Helpers helpers = new Helpers();

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
        diveCentersPagerAdapter = new DiveCentersPagerAdapter(this, getFragmentManager(), latLng, dsName, viewPager, tabLayout);
        viewPager.setAdapter(diveCentersPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void changeViewPagerItem () {

        this.viewPager.setCurrentItem(0);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    public static void show(Context context, LatLng latLng, String name) {
        Intent intent = new Intent(context, DiveCentersActivity.class);
        intent.putExtra("LATLNG", latLng);
        intent.putExtra("NAME", name);
        context.startActivity(intent);
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
