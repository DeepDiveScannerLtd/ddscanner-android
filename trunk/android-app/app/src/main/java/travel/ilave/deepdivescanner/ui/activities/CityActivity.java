package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.request.DiveSpotsRequestMap;
import travel.ilave.deepdivescanner.services.RegistrationIntentService;
import travel.ilave.deepdivescanner.ui.adapters.PlacesPagerAdapter;
import travel.ilave.deepdivescanner.ui.dialogs.SubscribeDialog;
import travel.ilave.deepdivescanner.utils.SharedPreferenceHelper;


public class CityActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "CityActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_PLACE_AUTOCOMPLETE = 1;
    private static final int REQUEST_CODE_FILTER = 2;

    private Toolbar toolbar;
    private ViewPager placeViewPager;
    private PlacesPagerAdapter placesPagerAdapter;
    private TabLayout tabLayout;
    private TextView toolbarTitle;
    private LatLng latLng;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton feedback;
    private SubscribeDialog subscribeDialog = new SubscribeDialog();
    private ProgressDialog progressDialog;


    public static void show(Context context, LatLng latLng) {
        Intent intent = new Intent(context, CityActivity.class);
        intent.putExtra("LATLNG", latLng);
        context.startActivity(intent);
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
            setContentView(R.layout.activity_city);
            findViews();
            toolbarSettings();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            latLng = getIntent().getParcelableExtra("LATLNG");
            toolbarTitle.setText("DDScanner");
            toolbarTitle.setOnClickListener(this);
            if (latLng != null && getCity(latLng) != null && !getCity(latLng).equals("")) {
                toolbarTitle.setText(getCity(latLng));
            }
            populatePlaceViewpager(latLng);
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
                }
            });
        }
    }

    private void populatePlaceViewpager(LatLng latLng) {
        placesPagerAdapter = new PlacesPagerAdapter(this, getFragmentManager(), latLng);
        placeViewPager.setAdapter(placesPagerAdapter);
        placeViewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(placeViewPager);
    }

    private void findViews() {
        tabLayout = (TabLayout) findViewById(R.id.place_sliding_tabs);
        placeViewPager = (ViewPager) findViewById(R.id.place_view_pager);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.filterButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        feedback = (FloatingActionButton) findViewById(R.id.feedbackFloat);
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
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.i(TAG, "Place: " + place.getName());
                    populatePlaceViewpager(place.getLatLng());
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
        getMenuInflater().inflate(R.menu.menu_city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openSearchLocationWindow();
                progressDialog.show();
                return true;
            case R.id.profile:
                if (SharedPreferenceHelper.getIsUserLogined()) {
                    Toast.makeText(CityActivity.this, "You are already login", Toast.LENGTH_LONG);
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
                subscribeDialog.show(getFragmentManager(), "");
                break;
            case R.id.filterButton:
                Intent intent = new Intent(this, FilterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FILTER);
//                FilterActivity.show(CityActivity.this);
                break;
            case R.id.toolbarTitle:
                openSearchLocationWindow();
                progressDialog.show();
                break;
        }
    }
}
