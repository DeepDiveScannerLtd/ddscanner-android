package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivityChangeAddressBinding;
import com.ddscanner.entities.Address;
import com.ddscanner.entities.Country;
import com.ddscanner.entities.CountryEntity;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class ChangeAddressActivity extends BaseAppCompatActivity {

    private ActivityChangeAddressBinding binding;
    private static final String ARG_COUNTRY ="country";
    private static final String ARG_ADDRESS = "address";
    private CountryEntity country;
    private Address address;
    private LatLng location;

    public static void showForResult(Activity context, int requestCode, String country, String address) {
        Intent intent = new Intent(context, ChangeAddressActivity.class);
        if (country !=null) {
            intent.putExtra(ARG_ADDRESS, address);
        }
        if (address != null) {
            intent.putExtra(ARG_COUNTRY, country);
        }
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_address);
        binding.setHandlers(this);
        if (getIntent().getStringExtra(ARG_COUNTRY) != null) {
            country = new Gson().fromJson(getIntent().getStringExtra(ARG_COUNTRY), CountryEntity.class);
        }
        if (getIntent().getStringExtra(ARG_ADDRESS) != null) {
            address = new Gson().fromJson(getIntent().getStringExtra(ARG_ADDRESS), Address.class);
            location = address.getPosition();
        }
        setupToolbar(R.string.location_address_edit, R.id.toolbar, R.menu.menu_change_address);
        setUi();
    }

    private void setUi() {
        if (country != null) {
            binding.countryTextView.setTextColor(ContextCompat.getColor(this, R.color.black_text));
            binding.countryTextView.setText(country.getName());
        }
        if (address != null) {
            binding.addressInput.setText(address.getName());
            binding.location.setText(DDScannerApplication.getInstance().getString(R.string.pattern_location_string, address.getLat().toString(), address.getLng().toString()));
        }
    }

    public void openMapClicked(View view) {
        PickLocationActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADDRESS_ACTIVITY_OPEN_MAP, location);
    }

    public void pickCountryClicked(View view) {
        PickCountryActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADDRESS_ACTIVITY_CHOOSE_COUNTRY);
    }

}
