package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ddscanner.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.List;
import java.util.Locale;

/**
 * Created by lashket on 5.4.16.
 */
public class PickLocationActivity extends AppCompatActivity implements GoogleMap.OnCameraChangeListener, View.OnClickListener {

    private static final int REQUEST_CODE_PLACE_AUTOCOMPLETE = 8001;
    private static final String TAG = PickLocationActivity.class.getSimpleName();

    private SupportMapFragment mapFragment;
    private TextView placeName;
    private TextView placeCoordinates;
    private GoogleMap googleMap;
    private Toolbar toolbar;
    private Geocoder geocoder;
    private LatLng pickedLatLng;
    private ChangeDataInTextView changeDataInTextView;
    private FloatingActionButton applyLocation;

    public static void show(Context context) {
        Intent intent = new Intent(context, PickLocationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_ds_location);
        geocoder = new Geocoder(this, Locale.ENGLISH);
        findViews();
        toolbarSettings();
        mapSettings();
    }

    private void findViews() {
        placeCoordinates = (TextView) findViewById(R.id.lat_lng);
        placeName = (TextView) findViewById(R.id.place_name);
        applyLocation = (FloatingActionButton) findViewById(R.id.apply_location);
        applyLocation.setOnClickListener(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        googleMap = mapFragment.getMap();
        googleMap.setOnCameraChangeListener(this);
    }

    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pick location");
    }

    private void mapSettings() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
            }
        });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        new ChangeDataInTextView().execute(cameraPosition.target);
        pickedLatLng = cameraPosition.target;
        placeCoordinates.setText(String.valueOf(cameraPosition.target.latitude) + ", " + String.valueOf(cameraPosition.target.longitude));

    }

    private String getCity(LatLng latLng) {
        String city = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                if (returnedAddress.getAddressLine(1) != null) {
                    city = returnedAddress.getAddressLine(1);
                } else if (returnedAddress.getAddressLine(2) != null) {
                    city = returnedAddress.getAddressLine(2);
                } else {
                    city = returnedAddress.getAddressLine(3);
                }

            }
        } catch (Exception e) {

        }

        return city;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLACE_AUTOCOMPLETE) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(place.getViewport(),0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pick_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.serch_picking_location:
                openSearchLocationWindow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_location:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("LATLNG", pickedLatLng);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }
    }

    private void openSearchLocationWindow() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, REQUEST_CODE_PLACE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, e.toString());
        }
    }

    private class ChangeDataInTextView extends AsyncTask<LatLng, Void, String> {

        @Override
        protected String doInBackground(LatLng... params) {
            return getCity(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                placeName.setText(s);
            }
        }
    }

}
