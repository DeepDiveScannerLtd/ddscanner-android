package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.databinding.ActivityPickLocationBinding;
import com.ddscanner.ui.views.MapControlView;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class PickLocationActivity extends BaseAppCompatActivity implements TextView.OnEditorActionListener, MapboxMap.OnCameraIdleListener {

    private ActivityPickLocationBinding binding;
    private boolean isNeedToRewrite = true;
    private static final String ARG_LOCATION = "location";
    private LatLng location;
    private MapboxMap mapboxMap;
    private MapControlView mapControlView;

    public static void showForResult(Activity context, int requestCode, LatLng latLng) {
        Intent intent = new Intent(context, PickLocationActivity.class);
        if (latLng != null) {
            intent.putExtra(ARG_LOCATION, latLng);
        }
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_location);
        binding.setHandlers(this);
        setupToolbar(R.string.pick_location, R.id.toolbar);
        if (getIntent().getParcelableExtra(ARG_LOCATION) != null) {
            location = getIntent().getParcelableExtra(ARG_LOCATION);
        }
        binding.mapFragment.onCreate(savedInstanceState);
        binding.mapFragment.getMapAsync(this::initMap);
        binding.longitude.setOnEditorActionListener(this);
        binding.latitude.setOnEditorActionListener(this);
    }
    
    private void initMap(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setOnCameraIdleListener(this);
        if (location != null) {
            this.mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        binding.mapControlLayout.appendWithMap(this.mapboxMap);
    }
    
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        this.googleMap = googleMap;
//        this.googleMap.setOnMapLoadedCallback(this);
//    }
//
//    @Override
//    public void onMapLoaded() {
//        this.googleMap.setOnCameraIdleListener(this);
//        if (location != null) {
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
//        }
//    }
//
//    @Override
//    public void onCameraIdle() {
//        if (isNeedToRewrite) {
//            binding.latitude.setText(String.valueOf(googleMap.getCameraPosition().target.latitude));
//            binding.longitude.setText(String.valueOf(googleMap.getCameraPosition().target.longitude));
//            return;
//        }
//        isNeedToRewrite = !isNeedToRewrite;
//    }


    @Override
    public void onCameraIdle() {
        if (isNeedToRewrite) {
            binding.latitude.setText(String.valueOf(mapboxMap.getCameraPosition().target.getLatitude()));
            binding.longitude.setText(String.valueOf(mapboxMap.getCameraPosition().target.getLongitude()));
            return;
        }
        isNeedToRewrite = !isNeedToRewrite;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapFragment.onResume();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        Double latitude = 0.0;
        Double longitude = 0.0;
        if (!binding.latitude.getText().toString().isEmpty()) {
            latitude = Double.parseDouble(binding.latitude.getText().toString());
        }
        if (!binding.longitude.getText().toString().isEmpty()) {
            longitude = Double.parseDouble(binding.longitude.getText().toString());
        }
        if ((latitude >= -180 && latitude <= 180) && (longitude >= -180 && longitude <= 180)) {
            isNeedToRewrite = false;
            mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        }
        return true;
    }

    public void saveLocationClicked(View view) {
        Intent returningIntent = new Intent();
        returningIntent.putExtra(ARG_LOCATION, mapboxMap.getCameraPosition().target);
        setResult(RESULT_OK, returningIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapFragment.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binding.mapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        binding.mapFragment.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();

        binding.mapFragment.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapFragment.onSaveInstanceState(outState);
    }
    
}
