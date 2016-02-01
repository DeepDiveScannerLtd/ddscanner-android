package travel.ilave.deepdivescanner.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.DiveSpot;
import travel.ilave.deepdivescanner.entities.DivespotsWrapper;
import travel.ilave.deepdivescanner.entities.Filters;
import travel.ilave.deepdivescanner.services.RegistrationIntentService;
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;
import travel.ilave.deepdivescanner.ui.dialogs.SubscribeDialog;


public class CityActivity extends AppCompatActivity {

    public static final String TAG = "CityActivity";

    private Toolbar toolbar;
    private ViewPager placeViewPager;
    private PlacesPagerAdapter placesPagerAdapter;
    private TabLayout tabLayout;
    private DivespotsWrapper divespotsWrapper;
    private LatLng latLng;
    private Filters filters = new Filters();
    private Map<String, String> map = new HashMap<String, String>();
    private BroadcastReceiver mRegistrationBroadcatReceiver;
    private FloatingActionButton floatingActionButton;
    private SubscribeDialog subscribeDialog = new SubscribeDialog();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasConnection(this)) {
            setContentView(R.layout.activity_city);
            findViews();
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_search_location);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            latLng = getIntent().getParcelableExtra("LATLNG");
            filters = getIntent().getParcelableExtra("FILTERS");
            mRegistrationBroadcatReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences.getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {

                    } else {
                        //Error with token
                    }
                }
            };

            if (checkPlayServices()) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
            populatePlaceViewpager(latLng);
            getSupportActionBar().setTitle(getCity(latLng));
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    FilterActivity.show(CityActivity.this, PlacesPagerAdapter.getLastLatlng());
                }
            });
        }
        else {
            setContentView(R.layout.activity_error);
            Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }
    }

    private void populatePlaceViewpager(LatLng latLng) {
        divespotsWrapper = new DivespotsWrapper();
        placesPagerAdapter = new PlacesPagerAdapter(this, getFragmentManager(), (ArrayList<DiveSpot>) divespotsWrapper.getDiveSpots(), latLng, filters);
        placeViewPager.setAdapter(placesPagerAdapter);
        placeViewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(placeViewPager);
    }

    private void findViews() {
        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        placeViewPager = (ViewPager) findViewById(R.id.place_view_pager);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.filterButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                populatePlaceViewpager(place.getLatLng());
                getSupportActionBar().setTitle(place.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                //SSLEngineResult.Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openSearchLocationWindow();
                return true;
            case R.id.profile:
                subscribeDialog.show(getFragmentManager(), "");
                return true;
            case R.id.logbook:
                subscribeDialog.show(getFragmentManager(), "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public static void show(Context context, LatLng latLng) {
        Intent intent = new Intent(context, CityActivity.class);
        intent.putExtra("LATLNG", latLng);
        context.startActivity(intent);
    }

    private void openSearchLocationWindow() {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, e.toString());
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

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}
