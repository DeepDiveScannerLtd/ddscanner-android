package com.ddscanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.ddscanner.R;
import com.ddscanner.entities.User;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.rest.RestClient;
import com.ddscanner.services.RegistrationIntentService;
import com.ddscanner.ui.adapters.PlacesPagerAdapter;
import com.ddscanner.ui.dialogs.SubscribeDialog;
import com.ddscanner.utils.EventTrackerHelper;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Handler;

import okhttp3.ResponseBody;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

public class CityActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    public static final String TAG = "CityActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_PLACE_AUTOCOMPLETE = 1;
    private static final int REQUEST_CODE_FILTER = 2;

    private Toolbar toolbar;
    public  static ViewPager placeViewPager;
    private PlacesPagerAdapter placesPagerAdapter;
    public static TabLayout tabLayout;
    private static TextView toolbarTitle;
    private LatLng latLng;
    private ImageButton floatingActionButton;
    private ImageButton feedback;
    private SubscribeDialog subscribeDialog = new SubscribeDialog();
    private ProgressDialog progressDialog;
    private RelativeLayout toast;
    private ProgressBar progressBar;
    private Map<String, Object> map = new HashMap<String, Object>();


    public static void show(Context context, LatLng latLng) {
        Intent intent = new Intent(context, CityActivity.class);
        intent.putExtra("LATLNG", latLng);
        context.startActivity(intent);
    }

    private void clearFilterSharedPrefences() {
        SharedPreferenceHelper.setObject("");
        SharedPreferenceHelper.setCurrents("");
        SharedPreferenceHelper.setVisibility("");
        SharedPreferenceHelper.setLevel("");
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasConnection(this)) {
            AppsFlyerLib.getInstance().startTracking(this, "5A7vyAMVwKT4RBiTaxrpSU");
            setContentView(R.layout.activity_city);
            findViews();
            toolbarSettings();
            clearFilterSharedPrefences();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            latLng = getIntent().getParcelableExtra("LATLNG");
            if (latLng == null) {
                latLng = new LatLng(0,0);
            }
            toolbarTitle.setText("DDScanner");
            toolbarTitle.setOnClickListener(this);
            LatLngBounds latLngBounds = new LatLngBounds(new LatLng(latLng.latitude - 0.1, latLng.longitude - 0.1), new LatLng(latLng.latitude + 0.1, latLng.longitude + 0.1));
            if (latLng != null && getCity(latLng) != null && !getCity(latLng).equals("")) {
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_LOCATION_DETERMINED, new HashMap<String, Object>() {{
                            put(EventTrackerHelper.PARAM_LOCATION_DETERMINED, latLng.toString());
                        }});
                toolbarTitle.setText(getCity(latLng));
            }
            populatePlaceViewpager(latLng, latLngBounds);
            playServices();

        } else {
            setContentView(R.layout.activity_error);
            Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            });
        }
    }

    private void populatePlaceViewpager(LatLng latLng, LatLngBounds latLngBounds) {
        placesPagerAdapter = new PlacesPagerAdapter(this, getFragmentManager(), latLng, latLngBounds, toast, progressBar, toolbarTitle );
        placeViewPager.setAdapter(placesPagerAdapter);
        placeViewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(placeViewPager);
    }

    private void findViews() {
        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        placeViewPager = (ViewPager) findViewById(R.id.place_view_pager);
        floatingActionButton = (ImageButton) findViewById(R.id.filterButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        feedback = (ImageButton) findViewById(R.id.feedbackFloat);
        toast = (RelativeLayout) findViewById(R.id.toast);
        progressBar = (ProgressBar) findViewById(R.id.request_progress);
        placeViewPager.setOnPageChangeListener(this);
        floatingActionButton.setOnClickListener(this);
        feedback.setOnClickListener(this);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void openSearchLocationWindow() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, REQUEST_CODE_PLACE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PLACE_AUTOCOMPLETE:
                if (resultCode == RESULT_OK) {
                    final Place place = PlaceAutocomplete.getPlace(this, data);
                    AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                            EventTrackerHelper.EVENT_PLACE_SEARCH_CHOSEN, new HashMap<String, Object>() {{
                                put(EventTrackerHelper.PARAM_PLACE_SEARCH_CHOSEN, place.getLatLng().toString());
                            }});
                    Log.i(TAG, "Place: " + place.getName() + " - " + place.getLatLng().toString());
                    populatePlaceViewpager(place.getLatLng(), place.getViewport());
                    progressDialog.dismiss();
                    toolbarTitle.setText(place.getAddress());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    progressDialog.dismiss();
                } else if (resultCode == RESULT_CANCELED) {
                    progressDialog.dismiss();
                }
                break;
            case REQUEST_CODE_FILTER:
                if (resultCode == RESULT_OK) {
                    placesPagerAdapter.requestDiveSpots(data.getStringExtra(DiveSpotsRequestMap.KEY_CURRENTS), data.getStringExtra(DiveSpotsRequestMap.KEY_LEVEL), data.getStringExtra(DiveSpotsRequestMap.KEY_OBJECT), data.getIntExtra(DiveSpotsRequestMap.KEY_RATING, -1), data.getStringExtra(DiveSpotsRequestMap.KEY_VISIBILITY));
                }
                break;
        }
    }


    public void playServices() {
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported");
                finish();
            }
            return false;
        }
        return true;
    }

    /* Get city by coordinates */
    private String getCity(LatLng latLng) {
        String city = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                if (returnedAddress.getAddressLine(1) != null) {
                    city = returnedAddress.getAddressLine(1);
                } else {
                    city = returnedAddress.getAddressLine(2);
                }

            }
        } catch (Exception e) {

        }

        return city;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (hasConnection(this)) {
            getMenuInflater().inflate(R.menu.menu_city, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_PLACE_SEARCH_OPENED, new HashMap<String, Object>());
                openSearchLocationWindow();
                progressDialog.show();
                return true;
            case R.id.profile:
                if (SharedPreferenceHelper.getIsUserLogined() && !SharedPreferenceHelper.getUserid().equals("")) {
                    ProfileActivity.show(CityActivity.this, getUserData());
                } else {
                    progressDialog.show();
                    SocialNetworks.show(CityActivity.this);
                    progressDialog.dismiss();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedbackFloat:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_LEAVE_FEEDBACK_CLICK, new HashMap<String, Object>());
                subscribeDialog.show(getFragmentManager(), "");
                break;
            case R.id.filterButton:
                Intent intent = new Intent(this, FilterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FILTER);
//                FilterActivity.show(CityActivity.this);
                break;
            case R.id.toolbarTitle:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_PLACE_SEARCH_OPENED, new HashMap<String, Object>());
                openSearchLocationWindow();
                progressDialog.show();
                break;
        }
    }

    public static void setTitle() {
        toolbarTitle.setText("DDScanner");
    }

    public static void checkGcm() {
        Call<ResponseBody> call = RestClient.getServiceInstance().identifyGcmToken(SharedPreferenceHelper.getGcmId());
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

   @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "Starting");

    }

    private User getUserData() {
        User user = new User();
        user.setId(SharedPreferenceHelper.getUserid());
        user.setPicture(SharedPreferenceHelper.getPhotolink());
        user.setType(SharedPreferenceHelper.getSn());
        user.setName(SharedPreferenceHelper.getUsername());
        user.setLink(SharedPreferenceHelper.getLink());
        return user;
    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, String.valueOf(position));
        switch (position) {
            case 0:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext(),
                        EventTrackerHelper.EVENT_DIVE_SITES_MAP_OPENED, new HashMap<String, Object>());
                break;
            case 1:
                AppsFlyerLib.getInstance().trackEvent(getApplicationContext().getApplicationContext(),
                        EventTrackerHelper.EVENT_DIVE_SITES_LIST_OPENED, new HashMap<String, Object>());
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
