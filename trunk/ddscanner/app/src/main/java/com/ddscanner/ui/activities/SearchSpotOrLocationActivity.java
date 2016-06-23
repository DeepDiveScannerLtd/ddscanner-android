package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.events.LocationChosedEvent;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.events.PlaceChoosedEvent;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.CustomPagerAdapter;
import com.ddscanner.ui.fragments.SearchDiveSpotsFragment;
import com.ddscanner.ui.fragments.SearchLocationFragment;
import com.ddscanner.utils.Constants;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lashket on 15.6.16.
 */
public class SearchSpotOrLocationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Menu menu;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SearchDiveSpotsFragment searchDiveSpotFragment = new SearchDiveSpotsFragment();
    private SearchLocationFragment searchLocationFragment = new SearchLocationFragment();
    private CustomPagerAdapter adapter;
    private Handler handler = new Handler();
    private List<String> placeList = new ArrayList<>();
    private static final int REQUEST_CODE_LOGIN = Constants.SEARCH_ACTIVITY_REQUEST_CODE_LOGIN;

    private GoogleApiClient googleApiClient;

    private RequestBody name; // query
    private RequestBody order; // sort by (name)
    private RequestBody sort; // asc - alphabet desc - nealphabet
    private RequestBody limit; // limit size
    private List<MultipartBody.Part> like = new ArrayList<>(); // name - оп имени
    private List<MultipartBody.Part> select = new ArrayList<>();// fields (id,name)
    private boolean isTryToOpenAddDiveSpotActivity = false;
    private long lastEnterDataInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_divespots);
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.search_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.search_tab_layout);
        setToolbarSettings(toolbar);
        setupViewPager();
    }

    private void setToolbarSettings(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
            getSupportActionBar().setTitle(getString(R.string.search));
        } catch (NullPointerException e) {
            findViews();
        }

    }

    private void setupViewPager() {
        adapter = new CustomPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(searchLocationFragment, getString(R.string.location));
        adapter.addFragment(searchDiveSpotFragment, getString(R.string.dive_spot));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    private void setupTabLayout() {
        try {
            tabLayout.getTabAt(0).setText(getString(R.string.location));
            tabLayout.getTabAt(1).setText(getString(R.string.dive_spot));
        } catch (NullPointerException e) {
            findViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sealife, menu);
        this.menu = menu;
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    public static void showForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SearchSpotOrLocationActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            name = RequestBody.create(MediaType.parse("multipart/form-data"), newText);
            createRequestBodyies();
            placeList = new ArrayList<String>();
            Places.GeoDataApi.getAutocompletePredictions(googleApiClient, newText, new LatLngBounds(new LatLng(-180, -180), new LatLng(180, 180)), null).setResultCallback(
                    new ResultCallback<AutocompletePredictionBuffer>() {
                        @Override
                        public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                            if (autocompletePredictions.getStatus().isSuccess()) {
                                for (AutocompletePrediction prediction : autocompletePredictions) {
                                    placeList.add(prediction.getPlaceId());
                                    Places.GeoDataApi.getPlaceById(googleApiClient, prediction.getPlaceId()).setResultCallback(new ResultCallback<PlaceBuffer>() {
                                        @Override
                                        public void onResult(PlaceBuffer places) {
                                            if (places.getStatus().isSuccess()) {
                                                try {
                                                    Place place = places.get(0);
                                                   // placeList.add(place);
                                                } catch (IllegalStateException e) {

                                                }
                                            }
                                            places.release();
                                        }
                                    });
                                   // searchLocationFragment.setList((ArrayList<Place>) placeList, googleApiClient);
                                }
                                searchLocationFragment.setList((ArrayList<String>) placeList, googleApiClient);
                            }
                        }
                    });

        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private void createRequestBodyies() {
        order = RequestBody.create(MediaType.parse("multipart/form-data"), "name");
        sort = RequestBody.create(MediaType.parse("multipart/form-data"), "asc");
        limit = RequestBody.create(MediaType.parse("multipart/form-data"), "5");
        like.add(MultipartBody.Part.createFormData("like[]", "name"));
        select.add(MultipartBody.Part.createFormData("select[]", "id"));
        select.add(MultipartBody.Part.createFormData("select[]", "name"));
        sendRequest();
    }

    private void sendRequest() {
        Call<ResponseBody> call = RestClient.getServiceInstance().getDivespotsByParameters(name, like, order, sort, limit, select);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                        DivespotsWrapper divespotsWrapper;
                        divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                        if (divespotsWrapper.getDiveSpots() != null) {
                            searchDiveSpotFragment.setDiveSpots((ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots());
                        }
                    } catch (IOException e) {

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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DDScannerApplication.bus.register(this);
        googleApiClient.connect();
    }

    private void setResultOfActivity(LatLngBounds latLngBounds) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("LATLNGBOUNDS", latLngBounds);
        setResult(RESULT_OK, returnIntent);
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
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void locationChosed(LocationChosedEvent event) {
        Places.GeoDataApi.getPlaceById(googleApiClient, event.getLatLngBounds()).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (places.getStatus().isSuccess()) {
                    try {
                        Place place = places.get(0);
                        if (place.getViewport() != null) {
                            setResultOfActivity(place.getViewport());
                        } else {
                            LatLngBounds latLngBounds = new LatLngBounds(new LatLng(place.getLatLng().latitude - 0.2, place.getLatLng().longitude - 0.2), new LatLng(place.getLatLng().latitude + 0.2, place.getLatLng().longitude + 0.2) );
                            setResultOfActivity(latLngBounds);
                        }
                        // placeList.add(place);
                    } catch (IllegalStateException e) {

                    }
                }
                places.release();
            }
        });
    }

    @Subscribe
    public void openLoginWindowToAdd(OpenAddDsActivityAfterLogin event) {
        isTryToOpenAddDiveSpotActivity = true;
        Intent intent = new Intent(SearchSpotOrLocationActivity.this, SocialNetworks.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isTryToOpenAddDiveSpotActivity) {
                        isTryToOpenAddDiveSpotActivity = false;
                        AddDiveSpotActivity.show(SearchSpotOrLocationActivity.this);
                    }
                }
                break;
        }
    }
}
