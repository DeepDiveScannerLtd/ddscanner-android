package com.ddscanner.screens.profile.edit.divecenter.search;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.ActivityNewDiveCenterBinding;
import com.ddscanner.entities.Address;
import com.ddscanner.entities.CountryEntity;
import com.ddscanner.entities.DiveCenterSearchItem;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.ChangeAddressActivity;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CreateDiveCenterActivity extends BaseAppCompatActivity {

    private static final String ARG_COUNTRY = "country";
    private static final String ARG_ADDRESS = "address";

    private ActivityNewDiveCenterBinding binding;
    private static final String ARG_DIVE_CENTER = "dive_center";
    private boolean isForEditDiveCenter = false;
    private MaterialDialog materialDialog;
    private int currentDiveCenterId;
    private int diveCenterType = 3;
    private String dcName;
    DiveCenterSearchItem diveCenterSearchItem;
    private CountryEntity countryEntity;
    private Address address;
    private Gson gson = new Gson();

    private DDScannerRestClient.ResultListener<Void> resultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            EventsTracker.trackInstructorRegistrationDcLegacyInvited();
            materialDialog.dismiss();
            Intent intent = new Intent();
            intent.putExtra(Constants.ARG_DC_TYPE, diveCenterType);
            intent.putExtra(Constants.ARG_ID, currentDiveCenterId);
            intent.putExtra(Constants.ARG_DC_NAME, dcName);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    private DDScannerRestClient.ResultListener<Integer> createNewResultListener = new DDScannerRestClient.ResultListener<Integer>() {
        @Override
        public void onSuccess(Integer result) {
            EventsTracker.trackInstructorRegistrationDcNewInvited();
            materialDialog.dismiss();
            Intent intent = new Intent();
            intent.putExtra(Constants.ARG_DC_TYPE, diveCenterType);
            intent.putExtra(Constants.ARG_ID, result);
            intent.putExtra(Constants.ARG_DC_NAME, dcName);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }
    };

    public static void showForCreateDiveCenter(Activity context, int requestCode) {
        Intent intent = new Intent(context, CreateDiveCenterActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void showForEditCurrentDiveCenter(Activity context, int requestCode, String currentData) {
        Intent intent = new Intent(context, CreateDiveCenterActivity.class);
        intent.putExtra(ARG_DIVE_CENTER, currentData);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_dive_center);
        binding.setHandlers(this);
        setupToolbar(R.string.new_dive_center, R.id.toolbar);
        materialDialog = Helpers.getMaterialDialog(this);
        if (getIntent().getStringExtra(ARG_DIVE_CENTER) != null) {
            diveCenterType = 2;
            isForEditDiveCenter = true;
            diveCenterSearchItem = gson.fromJson(getIntent().getStringExtra(ARG_DIVE_CENTER), DiveCenterSearchItem.class);
            currentDiveCenterId = diveCenterSearchItem.getId();
            binding.diveCenterEmail.setText(diveCenterSearchItem.getEmail());
            binding.diveCenterName.setText(diveCenterSearchItem.getName());
            if (diveCenterSearchItem.getAddresses() != null && diveCenterSearchItem.getCountry() != null) {
                countryEntity = diveCenterSearchItem.getCountry();
                address = diveCenterSearchItem.getAddresses().get(0);
                binding.addressLayout.setVisibility(View.VISIBLE);
                binding.address.setText(String.format("%s, %s", diveCenterSearchItem.getAddress(), diveCenterSearchItem.getCountry().getName()));
                binding.pickAddressButton.setVisibility(View.GONE);
            }
            if (diveCenterSearchItem.getPhoto() != null) {
                binding.diveCenterPhoto.setVisibility(View.VISIBLE);
                Picasso.with(this).load(DDScannerApplication.getInstance().getString(R.string.base_photo_url, diveCenterSearchItem.getPhoto(), "1")).placeholder(R.drawable.placeholder_photo_wit_round_corners).error(R.drawable.avatar_dc_profile_def).transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, this)), 0, RoundedCornersTransformation.CornerType.ALL)).into(binding.diveCenterPhoto);
            }
        } else {
            diveCenterSearchItem = new DiveCenterSearchItem();
        }
    }

    public void chooseAddressClicked(View view) {
        if (diveCenterSearchItem.getAddresses() != null) {
            ChangeAddressActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS, null, gson.toJson(diveCenterSearchItem.getAddresses().get(0)));
            return;
        }
        ChangeAddressActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS, null, null);
    }

    public void editAddressClicked(View view) {
        ChangeAddressActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS, gson.toJson(countryEntity), gson.toJson(address));
    }

    public void saveData(View view) {
        if (!isDataValid()) {
            return;
        }
        materialDialog.show();
        dcName = binding.diveCenterName.getText().toString();
        if (isForEditDiveCenter) {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).inviteLegacyDiveCenter(resultListener, binding.diveCenterName.getText().toString(), binding.diveCenterEmail.getText().toString(), currentDiveCenterId, gson.toJson(address), countryEntity.getCode(), DDScannerApplication.getInstance().getSharedPreferenceHelper().getDivecenterSearchSource().equals(SharedPreferenceHelper.SearchSourceType.REGISTRATION));
        } else {
            DDScannerApplication.getInstance().getDdScannerRestClient(this).inviteNewDiveCenter(createNewResultListener, binding.diveCenterName.getText().toString(), binding.diveCenterEmail.getText().toString(), gson.toJson(address), countryEntity.getCode(), DDScannerApplication.getInstance().getSharedPreferenceHelper().getDivecenterSearchSource().equals(SharedPreferenceHelper.SearchSourceType.REGISTRATION));
        }

    }

    private boolean validEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isDataValid() {
        boolean isDataValid = true;
        binding.nameError.setVisibility(View.GONE);
        binding.emailError.setVisibility(View.GONE);
        binding.addressError.setVisibility(View.GONE);
        if (binding.diveCenterName.getText().toString().length() < 1) {
            binding.nameError.setVisibility(View.VISIBLE);
            isDataValid = false;
        }
        if (!validEmail(binding.diveCenterEmail.getText().toString())) {
            binding.emailError.setVisibility(View.VISIBLE);
            isDataValid = false;
        }
        if (countryEntity == null || address == null) {
            binding.addressError.setVisibility(View.VISIBLE);
            isDataValid = false;
        }
        return isDataValid;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS:
                if (resultCode == RESULT_OK) {
                    address = (Address) data.getSerializableExtra(ARG_ADDRESS);
                    countryEntity = (CountryEntity) data.getSerializableExtra(ARG_COUNTRY);
                    binding.address.setText(String.format("%s, %s", address.getName(), countryEntity.getName()));
                    binding.addressLayout.setVisibility(View.VISIBLE);
                    binding.pickAddressButton.setVisibility(View.GONE);
                }
                break;
        }
    }
}
