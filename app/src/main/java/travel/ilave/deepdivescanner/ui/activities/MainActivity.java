package travel.ilave.deepdivescanner.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
import travel.ilave.deepdivescanner.utils.LogUtils;

public class    MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private Spinner citiesSpinner;
    private Spinner licensesSpinner;
    private FloatingActionButton searchFab;
    private CitiesLicensesWrapper citiesLicensesWrapper;
    private ProgressDialog progressDialog;
    private Button btnError;

    private int selectedCityPosition = -1;
    private int selectedLicensePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isOnline()) {
            setContentView(R.layout.activity_main);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);

            citiesSpinner = (Spinner) findViewById(R.id.main_cities_spinner);
            citiesSpinner.setOnItemSelectedListener(this);
            licensesSpinner = (Spinner) findViewById(R.id.main_licenses_spinner);
            licensesSpinner.setOnItemSelectedListener(this);
            searchFab = (FloatingActionButton) findViewById(R.id.main_search_fab);
            searchFab.setOnClickListener(this);
      /*  if (!isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.errorConnection, Toast.LENGTH_LONG).show();
            searchFab.hide();
        } else {
            requestCities();
        }*/
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
        progressDialog.setMessage("Please wait");
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
                if(error.getKind().equals(RetrofitError.Kind.HTTP)) {
                    Toast.makeText(MainActivity.this, R.string.serverNotResp, Toast.LENGTH_LONG);
                }
                // TODO Handle result handling when activity stopped
                // TODO Handle errors
            }
        });
    }

    private void populateCitiesSpinner(List<City> cities) {
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(this, R.layout.item_spinner, android.R.id.text1, cities);
        citiesSpinner.setAdapter(adapter);
    }

    private void populateLicensesSpinner(List<String> licenses) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, android.R.id.text1, licenses);
        licensesSpinner.setAdapter(adapter);
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        if (selectedCityPosition == -1) {
            Toast.makeText(this, R.string.choseCity, Toast.LENGTH_SHORT).show();
        }
        if (selectedCityPosition == -1) {
            Toast.makeText(this, R.string.choseLicense, Toast.LENGTH_SHORT).show();
        }
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

}
