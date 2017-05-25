package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotChosedFromSearch;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.events.GoToMyLocationButtonClickedEvent;
import com.ddscanner.events.LocationChosedEvent;
import com.ddscanner.events.OpenAddDsActivityAfterLogin;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.add.AddDiveSpotActivity;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.adapters.CustomPagerAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.fragments.SearchDiveSpotsFragment;
import com.ddscanner.ui.fragments.SearchLocationFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SearchSpotOrLocationActivity extends BaseAppCompatActivity implements SearchView.OnQueryTextListener, ViewPager.OnPageChangeListener, DialogClosedListener {

    private static final String TAG = SearchSpotOrLocationActivity.class.getName();

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

    private boolean isTryToOpenAddDiveSpotActivity = false;
    private Runnable sendingSearchRequestRunnable;
    private boolean isForDiveCenter;

    private ProgressView progressView;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>> divespotsWrapperResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotShort> result) {
            searchDiveSpotFragment.setDiveSpotShorts(result);
            progressView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_divespots);
        isForDiveCenter = getIntent().getBooleanExtra("isfordivecenter", false);
        if (!isForDiveCenter) {
            googleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        findViews();
    }

    private void findViews() {
        progressView = (ProgressView) findViewById(R.id.progress_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.search_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.search_tab_layout);
        if (isForDiveCenter) {
            tabLayout.setVisibility(View.GONE);
        }
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
        if (!isForDiveCenter) {
            adapter.addFragment(searchLocationFragment, getString(R.string.location));
        }
        adapter.addFragment(searchDiveSpotFragment, getString(R.string.dive_spot));
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        EventsTracker.trackSearchByLocation();
        if (!isForDiveCenter) {
            tabLayout.setupWithViewPager(viewPager);
            setupTabLayout();
        }
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
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconified(false);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    public static void showForResult(Activity activity, int requestCode, boolean isForDiveCenter) {
        Intent intent = new Intent(activity, SearchSpotOrLocationActivity.class);
        intent.putExtra("isfordivecenter", isForDiveCenter);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        tryToSendRquest(newText);
        return true;
    }

    private void tryToSendRquest(final String newText) {
        handler.removeCallbacks(sendingSearchRequestRunnable);
        sendingSearchRequestRunnable = () -> {
            if (!newText.isEmpty()) {
                viewPager.setVisibility(View.GONE);
                progressView.setVisibility(View.VISIBLE);
                DDScannerApplication.getInstance().getDdScannerRestClient(SearchSpotOrLocationActivity.this).getDivespotsByName(newText, divespotsWrapperResultListener);
                if (!isForDiveCenter) {
                    placeList = new ArrayList<String>();
                    Places.GeoDataApi.getAutocompletePredictions(googleApiClient, newText, new LatLngBounds(new LatLng(-180, -180), new LatLng(180, 180)), null).setResultCallback(
                            autocompletePredictions -> {
                                if (autocompletePredictions.getStatus().isSuccess()) {
                                    for (AutocompletePrediction prediction : autocompletePredictions) {
                                        placeList.add(prediction.getPlaceId());
                                        Places.GeoDataApi.getPlaceById(googleApiClient, prediction.getPlaceId()).setResultCallback(places -> {
                                            if (places.getStatus().isSuccess()) {
                                                try {
                                                    Place place = places.get(0);
                                                    // placeList.add(place);
                                                } catch (IllegalStateException e) {

                                                }
                                            }
                                            places.release();
                                        });
                                        // searchLocationFragment.setList((ArrayList<Place>) placeList, googleApiClient);
                                    }
                                    searchLocationFragment.setList((ArrayList<String>) placeList, googleApiClient);
                                }
                            });
                }
            }
        };
        handler.postDelayed(sendingSearchRequestRunnable, 630);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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
//        DDScannerApplication.bus.register(this);
        if (!isForDiveCenter) {
            googleApiClient.connect();
        }
    }

    private void setResultOfActivity(LatLngBounds latLngBounds) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.SEARCH_ACTIVITY_INTENT_KEY, latLngBounds);
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
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        DDScannerApplication.activityResumed();
    }

    @Override
    public void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
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
                    } catch (IllegalStateException ignored) {

                    }
                }
                places.release();
            }
        });
    }

    @Subscribe
    public void openLoginWindowToAdd(OpenAddDsActivityAfterLogin event) {
        isTryToOpenAddDiveSpotActivity = true;
        Intent intent = new Intent(SearchSpotOrLocationActivity.this, LoginActivity.class);
        startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_SEARCH_ACTIVITY_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_SEARCH_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK) {
                    if (isTryToOpenAddDiveSpotActivity) {
                        isTryToOpenAddDiveSpotActivity = false;
                        EventsTracker.trackDiveSpotCreation();
                        AddDiveSpotActivity.show(SearchSpotOrLocationActivity.this);
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_SEARCH_DIVE_SPOT_ADD_SPOT:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_CODE_DIVE_SPOT_ADDED, data);
                    finish();
                }
                break;
        }
    }

    @Subscribe
    public void goToMyLocation(GoToMyLocationButtonClickedEvent event) {
        setResult(ActivitiesRequestCodes.RESULT_CODE_SEARCH_ACTIVITY_MY_LOCATION);
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                EventsTracker.trackSearchByLocation();
                break;
            case 1:
                EventsTracker.trackSearchByDiveSpot();
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_SEARCH_ACTIVITY_UNEXPECTED_ERROR:
            case DialogsRequestCodes.DRC_SEARCH_ACTIVITY_FAILED_TO_CONNECT:
                finish();
                break;
        }
    }

    @Subscribe
    public void diveSpotChosed(DiveSpotChosedFromSearch event) {
        if (isForDiveCenter) {
            Intent intent = new Intent();
            intent.putExtra("divespot",event.getDiveSpot());
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        DiveSpotDetailsActivity.show(this, String.valueOf(event.getDiveSpot().getId()), EventsTracker.SpotViewSource.FROM_SEARCH);
    }

}
