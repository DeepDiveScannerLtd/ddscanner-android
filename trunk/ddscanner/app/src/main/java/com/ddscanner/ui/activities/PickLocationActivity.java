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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class PickLocationActivity extends BaseAppCompatActivity implements TextView.OnEditorActionListener, OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnCameraIdleListener {

    private ActivityPickLocationBinding binding;
    private GoogleMap googleMap;
    private boolean isNeedToRewrite = true;
    private static final String ARG_LOCATION = "location";
    private LatLng location;

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
        binding.mapFragment.onCreate(null);
        binding.mapFragment.getMapAsync(this);
        binding.longitude.setOnEditorActionListener(this);
        binding.latitude.setOnEditorActionListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        this.googleMap.setOnCameraIdleListener(this);
        if (location != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    @Override
    public void onCameraIdle() {
        if (isNeedToRewrite) {
            binding.latitude.setText(String.valueOf(googleMap.getCameraPosition().target.latitude));
            binding.longitude.setText(String.valueOf(googleMap.getCameraPosition().target.longitude));
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
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        }
        return true;
    }

    public void saveLocationClicked(View view) {
        Intent returningIntent = new Intent();
        returningIntent.putExtra(ARG_LOCATION, googleMap.getCameraPosition().target);
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
}
