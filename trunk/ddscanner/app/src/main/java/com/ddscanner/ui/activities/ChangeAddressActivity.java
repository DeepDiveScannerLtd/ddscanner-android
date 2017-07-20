package com.ddscanner.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.ActivityChangeAddressBinding;
import com.ddscanner.entities.Address;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.CountryEntity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class ChangeAddressActivity extends BaseAppCompatActivity {

    private ActivityChangeAddressBinding binding;
    private static final String ARG_COUNTRY ="country";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_LOCATION = "location";
    private CountryEntity country;
    private Address address;
    private LatLng location;
    private MenuItem menuItem;
    private boolean isCountryChosed = false;
    private boolean isLocationChosed = false;
    private SpannableString menuTitleSpannableString;

    public static void showForResult(Activity context, int requestCode, String country, String address) {
        Intent intent = new Intent(context, ChangeAddressActivity.class);
        if (address != null) {
            intent.putExtra(ARG_ADDRESS, address);
        }
        if (country != null) {
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
            if (address.getLat() != null && address.getLng() != null) {
                location = address.getPosition();
            }
        }
        setupToolbar(R.string.location_address_edit, R.id.toolbar, R.menu.menu_change_address);
        setUi();
    }

    private void setUi() {
        if (country != null) {
            isCountryChosed = true;
            binding.countryTextView.setTextColor(ContextCompat.getColor(this, R.color.black_text));
            binding.countryTextView.setText(country.getName());
        }
        if (address != null) {
            binding.addressInput.setText(address.getName());
            if (address.getLat() != null) {
                isLocationChosed = true;
                binding.location.setText(DDScannerApplication.getInstance().getString(R.string.pattern_location_string, address.getLat().toString(), address.getLng().toString()));
            }
        }
    }

    public void openMapClicked(View view) {
        PickLocationActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADDRESS_ACTIVITY_OPEN_MAP, location);
    }

    public void pickCountryClicked(View view) {
        PickCountryActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADDRESS_ACTIVITY_CHOOSE_COUNTRY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ADDRESS_ACTIVITY_CHOOSE_COUNTRY:
                if (resultCode == RESULT_OK) {
                    BaseIdNamePhotoEntity entity = (BaseIdNamePhotoEntity) data.getSerializableExtra(ARG_COUNTRY);
                    if (country == null) {
                        country = new CountryEntity();
                    }
                    country.setName(entity.getName());
                    country.setCode(entity.getCode());
                    isCountryChosed = true;
                    setUi();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADDRESS_ACTIVITY_OPEN_MAP:
                if (resultCode == RESULT_OK) {
                    LatLng location;
                    location = data.getParcelableExtra(ARG_LOCATION);
                    address = new Address(binding.addressInput.getText().toString(), location.latitude, location.longitude);
                    setUi();
                    isLocationChosed = true;
                }
                break;
        }
    }

    private boolean isDataEntered() {
        if (!isCountryChosed || !isLocationChosed || binding.addressInput.getText().toString().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (isDataEntered()) {
                    Intent intent = new Intent();
                    address.setName(binding.addressInput.getText().toString());
                    intent.putExtra(ARG_COUNTRY, country);
                    intent.putExtra(ARG_ADDRESS, address);
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }
                UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.empty_string, R.string.popup_address_error, false);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }
}
