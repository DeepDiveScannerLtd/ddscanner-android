package com.ddscanner.ui.activities;

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

import com.ddscanner.R;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.CustomPagerAdapter;
import com.ddscanner.ui.fragments.SearchDiveSpotsFragment;
import com.ddscanner.ui.fragments.SearchLocationFragment;
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

    private GoogleApiClient googleApiClient;

    private RequestBody name; // query
    private RequestBody order; // sort by (name)
    private RequestBody sort; // asc - alphabet desc - nealphabet
    private RequestBody limit; // limit size
    private List<MultipartBody.Part> like = new ArrayList<>(); // name - оп имени
    private List<MultipartBody.Part> select = new ArrayList<>(); // fields (id,name)
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

    public static void show(Context context) {
        Intent intent = new Intent(context, SearchSpotOrLocationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            switch (viewPager.getCurrentItem()) {
                case 1:
                    name = RequestBody.create(MediaType.parse("multipart/form-data"), newText);
                    createRequestBodyies();
                    break;
                case 0:
                    List<Integer> filterTypes = new ArrayList<Integer>();
                    placeList = new ArrayList<String>();
                    filterTypes.add(Place.TYPE_ESTABLISHMENT);
                    Places.GeoDataApi.getAutocompletePredictions(googleApiClient, newText, new LatLngBounds(new LatLng(-180, -180), new LatLng(180, 180)), null).setResultCallback(
                            new ResultCallback<AutocompletePredictionBuffer>() {
                                @Override
                                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                                    if (autocompletePredictions.getStatus().isSuccess()) {
                                        for (AutocompletePrediction prediction : autocompletePredictions) {
                                            Log.i("ADA", prediction.getPlaceId());
                                            placeList.add(prediction.getPlaceId());
                                        }
                                        searchLocationFragment.setList((ArrayList<String>) placeList, googleApiClient);
                                    }
                                }
                            });
                    break;
            }
        }
        return false;
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
        googleApiClient.connect();
    }
}
