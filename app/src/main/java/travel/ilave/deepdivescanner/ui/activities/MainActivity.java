package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import travel.ilave.deepdivescanner.R;
import travel.ilave.deepdivescanner.entities.CitiesLicensesWrapper;
import travel.ilave.deepdivescanner.entities.City;
import travel.ilave.deepdivescanner.rest.RestClient;
import travel.ilave.deepdivescanner.services.RegistrationIntentService;
import travel.ilave.deepdivescanner.utils.LogUtils;
import travel.ilave.deepdivescanner.utils.SharedPreferenceHelper;

public class    MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private Spinner citiesSpinner;
    private Spinner licensesSpinner;
    private FloatingActionButton searchFab;
    private CitiesLicensesWrapper citiesLicensesWrapper;
    private ProgressDialog progressDialog;
    private Button btnError;
    private BroadcastReceiver mRegistrationBroadcatReceiver;
    private SharedPreferences sPref;

    private int selectedCityPosition = -1;
    private int selectedLicensePosition = -1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private static final String CITY = "CITY";
    private static final String LICENSE = "LICENSE";
    private String lastCity;
    private String lastLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lastCity = SharedPreferenceHelper.loadPref(CITY);
        lastLicense = SharedPreferenceHelper.loadPref(LICENSE);

        if (isOnline()) {
            setContentView(R.layout.activity_main);

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

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);

            citiesSpinner = (Spinner) findViewById(R.id.main_cities_spinner);
            citiesSpinner.setOnItemSelectedListener(this);
            licensesSpinner = (Spinner) findViewById(R.id.main_licenses_spinner);
            licensesSpinner.setOnItemSelectedListener(this);
            searchFab = (FloatingActionButton) findViewById(R.id.main_search_fab);
            searchFab.setOnClickListener(this);
            requestCities();
            } else {
            setContentView(R.layout.activity_error);
            btnError = (Button) findViewById(R.id.btnRefresh);
            btnError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = getIntent();
                    finish();
                    startActivity(i);
                }
            });
        }
    }

    private void requestCities() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.pleaseWait));
        progressDialog.show();

        RestClient.getServiceInstance().getCities(new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {

                String responseString = new String(((TypedByteArray) s.getBody()).getBytes());
                LogUtils.i("response code is " + s.getStatus());
                LogUtils.i("response body is " + responseString);
                // TODO Handle result handling when activity stopped
                citiesLicensesWrapper = new Gson().fromJson(responseString, CitiesLicensesWrapper.class);
                populateCitiesSpinner(citiesLicensesWrapper.getCities());
                populateLicensesSpinner(citiesLicensesWrapper.getLicences());
            }

            @Override
            public void failure(RetrofitError error) {
                LogUtils.i("failure Message is " + error.getMessage());
                LogUtils.i("failure body is " + error.getBody());
                if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(MainActivity.this, R.string.serverNotResp, Toast.LENGTH_LONG);
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void populateCitiesSpinner(List<City> cities) {
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(this, R.layout.item_spinner, android.R.id.text1, cities);
        int lastPos = getLastCheckedCity(adapter);
        citiesSpinner.setAdapter(adapter);
        citiesSpinner.setSelection(lastPos);
    }

    private void populateLicensesSpinner(List<String> licenses) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, android.R.id.text1, licenses);
        licensesSpinner.setAdapter(adapter);
        int lastPos = adapter.getPosition(lastLicense);
        licensesSpinner.setSelection(lastPos);
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcatReceiver,
                new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcatReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (selectedCityPosition == -1) {
            Toast.makeText(this, R.string.choseCity, Toast.LENGTH_SHORT).show();
        }
        if (selectedLicensePosition == -1) {
            Toast.makeText(this, R.string.choseLicense, Toast.LENGTH_SHORT).show();
        }

        SharedPreferenceHelper.saveLicenseCity(citiesSpinner.getItemAtPosition(selectedCityPosition).toString(),
                licensesSpinner.getItemAtPosition(selectedLicensePosition).toString());
        CityActivity.show(this, citiesLicensesWrapper.getCities().get(selectedCityPosition), citiesLicensesWrapper.getLicences().get(selectedLicensePosition));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.main_cities_spinner:
                selectedCityPosition = i;
                break;
            case R.id.main_licenses_spinner:
                selectedLicensePosition = i;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static void show(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /* Checking internet connection */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
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

    private int getLastCheckedCity (Adapter adapter) {
        int i = 0, lastPos = -1;
        while ((lastPos == -1) && (i < adapter.getCount())) {
            if(adapter.getItem(i).toString().equals(lastCity)) {
                lastPos = i;
            }
            i++;
        }
        return lastPos;
    }

}
