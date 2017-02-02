package com.ddscanner.screens.profile.edit;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.EditDcProfileViewBinding;
import com.ddscanner.entities.DialogClosedListener;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.Language;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.PickLanguageActivity;
import com.ddscanner.ui.activities.SearchSpotOrLocationActivity;
import com.ddscanner.ui.adapters.DiveCenterLanguagesListAdapter;
import com.ddscanner.ui.adapters.DiveSpotsListForEditDcAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class EditDiveCenterProfileActivity extends BaseAppCompatActivity implements BaseAppCompatActivity.PictureTakenListener, DialogClosedListener {

    private ArrayList<EditText> phonesEditTexts = new ArrayList<>();
    private ArrayList<EditText> emailsEditTexts = new ArrayList<>();
    private ArrayList<TextView> phonesErrors = new ArrayList<>();
    private ArrayList<TextView> emailsErrors = new ArrayList<>();
    private List<MultipartBody.Part> emails =  new ArrayList<>();
    private List<MultipartBody.Part> phones = new ArrayList<>();
    private List<MultipartBody.Part> languages = null;
    private List<MultipartBody.Part> diveSpots = null;
    private EditText countryEdiText;
    private EditText addressEditText;
    private String imagePath;
    private MultipartBody.Part photo = null;
    private String countryCode = null;
    private String locationLatitude = null;
    private String locationLongitude = null;
    private RequestBody countryRequestBody = null, addressRequestBody = null, nameRequestBody = null;
    private DiveSpotsListForEditDcAdapter diveSpotsListForEditDcAdapter = new DiveSpotsListForEditDcAdapter();
    private DiveCenterLanguagesListAdapter languagesListAdapter = new DiveCenterLanguagesListAdapter();
    private String country;
    private MaterialDialog materialDialog;
    private boolean isHaveSpots;
    private boolean isLanguagesDownloaded = false;
    private boolean isDiveSpotsDownloaded = false;

    private EditDcProfileViewBinding binding;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>> diveSpotsResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotShort>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotShort> result) {
            diveSpotsListForEditDcAdapter.addAllDiveSpots(result);
            isDiveSpotsDownloaded = true;
            dismissMaterialDiaog();
        }

        @Override
        public void onConnectionFailure() {
            isDiveSpotsDownloaded = true;
            dismissMaterialDiaog();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            isDiveSpotsDownloaded = true;
            dismissMaterialDiaog();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            isDiveSpotsDownloaded = true;
            dismissMaterialDiaog();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
        }

    };

    private DDScannerRestClient.ResultListener<ArrayList<Language>> languagesResultListener = new DDScannerRestClient.ResultListener<ArrayList<Language>>() {
        @Override
        public void onSuccess(ArrayList<Language> result) {
            languagesListAdapter.addAllLanguages(result);
            isLanguagesDownloaded = true;
            dismissMaterialDiaog();
        }

        @Override
        public void onConnectionFailure() {
            isLanguagesDownloaded = true;
            dismissMaterialDiaog();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            isLanguagesDownloaded = true;
            dismissMaterialDiaog();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            isLanguagesDownloaded = true;
            dismissMaterialDiaog();
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
        }

    };

    private DDScannerRestClient.ResultListener<Void> resultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            materialDialog.dismiss();
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            materialDialog.dismiss();

        }

        @Override
        public void onInternetConnectionClosed() {
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    private void dismissMaterialDiaog() {
        if (isDiveSpotsDownloaded && isLanguagesDownloaded) {
            materialDialog.dismiss();
        }
    }

    private void cancelAllResultListenersAndCloseActivity() {
        languagesResultListener.setCancelled(true);
        diveSpotsResultListener.setCancelled(true);
        finish();
    }

    public static void showForResult(Activity context, String diveCenterString, int requestCode, boolean isHaveSpots) {
        Intent intent = new Intent(context, EditDiveCenterProfileActivity.class);
        intent.putExtra("divecenter", diveCenterString);
        intent.putExtra("isspots", isHaveSpots);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.edit_dc_profile_view);
        isHaveSpots = getIntent().getBooleanExtra("isspots", false);
        binding.setHandlers(this);
        binding.setDcViewModel(new EditDiveCenterProfileActivityViewModel(new Gson().fromJson(getIntent().getStringExtra("divecenter"), DiveCenterProfile.class)));
        countryCode = binding.getDcViewModel().getDiveCenterProfile().getCountryCode();
        setupToolbar(R.string.edit_profile_activity, R.id.toolbar, R.menu.edit_profile_menu);
        setupUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setupUi() {
        materialDialog = Helpers.getMaterialDialog(this);
        materialDialog.show();
        if (isHaveSpots) {
            DDScannerApplication.getInstance().getDdScannerRestClient().getSelfDiveCenterDiveSpotsList(diveSpotsResultListener);
        } else {
            isDiveSpotsDownloaded = true;
        }
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveCenterLanguages(languagesResultListener);
        binding.diveSpotList.setLayoutManager(new LinearLayoutManager(this));
        binding.diveSpotList.setAdapter(diveSpotsListForEditDcAdapter);
        binding.languagesList.setLayoutManager(new LinearLayoutManager(this));
        binding.languagesList.setAdapter(languagesListAdapter);
        if (binding.getDcViewModel().getDiveCenterProfile().getWorkingSpots() != null) {
            diveSpotsListForEditDcAdapter.addAllDiveSpots(binding.getDcViewModel().getDiveCenterProfile().getWorkingSpots());
        }
        if (binding.getDcViewModel().getDiveCenterProfile().getPhones() == null) {
            addPhoneClicked(null);
        } else {
            for (String phone : binding.getDcViewModel().getDiveCenterProfile().getPhones()) {
                addPhoneView(phone);
            }
        }
        if (binding.getDcViewModel().getDiveCenterProfile().getEmails() == null) {
            addEmailClicked(null);
        } else {
            for (String email : binding.getDcViewModel().getDiveCenterProfile().getEmails()) {
                addEmailView(email);
            }
        }
        if (binding.getDcViewModel().getDiveCenterProfile().getAddresses() != null) {
            locationLatitude = String.valueOf(binding.getDcViewModel().getDiveCenterProfile().getAddresses().get(0).getLat());
            locationLongitude = String.valueOf(binding.getDcViewModel().getDiveCenterProfile().getAddresses().get(0).getLng());
            addAddressesView(binding.getDcViewModel().getDiveCenterProfile().getAddresses().get(0).getName(), binding.getDcViewModel().getDiveCenterProfile().getCountryName());
        }
    }

    public void addEmailClicked(View view) {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_dive_center_email_edit_text, null);
        EditText editText = (EditText) linearLayout.findViewById(R.id.email);
        TextView textView = (TextView) linearLayout.findViewById(R.id.email_error);
        emailsErrors.add(textView);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailsEditTexts.add(editText);
        binding.emails.addView(linearLayout);
    }

    public void addPhoneClicked(View view) {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_dive_center_edit_text, null);
        EditText editText =(EditText) linearLayout.findViewById(R.id.phone);
        TextView textView = (TextView) linearLayout.findViewById(R.id.phone_error);
        phonesErrors.add(textView);
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phonesEditTexts.add(editText);
        binding.phones.addView(linearLayout);
    }

    private void addEmailView(String text) {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_dive_center_email_edit_text, null);
        EditText editText = (EditText) linearLayout.findViewById(R.id.email);
        TextView textView = (TextView) linearLayout.findViewById(R.id.email_error);
        emailsErrors.add(textView);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailsEditTexts.add(editText);
        editText.setText(text);
        binding.emails.addView(linearLayout);
    }

    private void addPhoneView(String text) {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_dive_center_edit_text, null);
        EditText editText =(EditText) linearLayout.findViewById(R.id.phone);
        TextView textView = (TextView) linearLayout.findViewById(R.id.phone_error);
        phonesErrors.add(textView);
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phonesEditTexts.add(editText);
        editText.setText(text);
        binding.phones.addView(linearLayout);
    }

    public void addDiveSpotClicked(View view) {
        SearchSpotOrLocationActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_SPOT, true);
    }

    public void chooseAddressClicked(View view) {
        showPickPlaceActivity();
    }

    public void pickCountryClicked(View view) {
    }

    public void changePhotoButtonCLicked(View view) {
        pickSinglePhotoFromGallery();
    }

    private void showPickPlaceActivity() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_PICK_LOCATION);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        this.imagePath = pictures.get(0);
        Picasso.with(this).load("file://" + pictures.get(0)).resize(Math.round(Helpers.convertDpToPixel(65, this)),Math.round(Helpers.convertDpToPixel(65, this))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, this)),0, RoundedCornersTransformation.CornerType.ALL)).into(binding.pickPhotos);
    }

    @Override
    public void onPictureFromCameraTaken(File picture) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_SPOT:
                if (resultCode == RESULT_OK) {
                    diveSpotsListForEditDcAdapter.addDiveSpot((DiveSpotShort) data.getSerializableExtra("divespot"));
                }
                break;
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_PICK_LOCATION:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
                    List<Address> addresses = new ArrayList<>();
                    try {
                        addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        locationLongitude = String.valueOf(place.getLatLng().longitude);
                        locationLatitude = String.valueOf(place.getLatLng().latitude);
                        Log.i("Country name", addresses.get(0).getCountryName());
                        Log.i("Country code", addresses.get(0).getCountryCode());
                        if (place.getAddress() != null) {
                            addAddressesView(place.getAddress().toString(), addresses.get(0).getCountryName());
                            countryCode = addresses.get(0).getCountryCode();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_LANGUAGE:
                if (resultCode == RESULT_OK) {
                    languagesListAdapter.addLanguage((Language) data.getSerializableExtra("language"));
                }
                break;

        }
    }

    public void addLanguageClicked(View view) {
        PickLanguageActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_LANGUAGE);
    }

    private void addAddressesView(String address, String country) {
        if (countryEdiText == null) {
            countryEdiText = (EditText) LayoutInflater.from(this).inflate(R.layout.edit_dive_center_address_edit_text, null);
            countryEdiText.setHint("Country");
            countryEdiText.setEnabled(false);
            binding.addresses.addView(countryEdiText);
        }
        countryEdiText.setText(country);
        if (addressEditText == null) {
            addressEditText  = (EditText) LayoutInflater.from(this).inflate(R.layout.edit_dive_center_address_edit_text, null);
            binding.addresses.addView(addressEditText);
        }
        addressEditText.setText(address);
    }

    public void saveChangesClicked(View view) {

        if (!isDataValid()) {
            return;
        }
        if (!binding.name.getText().toString().isEmpty()) {
            nameRequestBody = Helpers.createRequestBodyForString(binding.name.getText().toString());
        }

        for (EditText editText : phonesEditTexts) {
            if (!editText.getText().toString().isEmpty()) {
                phones.add(MultipartBody.Part.createFormData("phones[]", editText.getText().toString()));
            }
        }

        for (EditText editText : emailsEditTexts) {
            if (!editText.getText().toString().isEmpty()) {
                emails.add(MultipartBody.Part.createFormData("emails[]", editText.getText().toString()));
            }
        }

        if (locationLatitude != null && locationLongitude != null && addressEditText != null && !addressEditText.getText().toString().isEmpty()) {
            com.ddscanner.entities.Address address = new com.ddscanner.entities.Address(addressEditText.getText().toString(), Double.valueOf(locationLatitude), Double.valueOf(locationLongitude));
            ArrayList<com.ddscanner.entities.Address> addresses = new ArrayList<>();
            addresses.add(address);
            addressRequestBody = Helpers.createRequestBodyForString(new Gson().toJson(addresses));
        }

        if (languagesListAdapter.getObjects().size() > 0) {
            languages = new ArrayList<>();
            for (Language language : languagesListAdapter.getObjects()) {
                languages.add(MultipartBody.Part.createFormData("content_lang[]", language.getCode()));
            }
        }

        if (diveSpotsListForEditDcAdapter.getObjects().size() > 0) {
            diveSpots = new ArrayList<>();
            for (DiveSpotShort diveSpotShort : diveSpotsListForEditDcAdapter.getObjects()) {
                diveSpots.add(MultipartBody.Part.createFormData("dive_spots[]", String.valueOf(diveSpotShort.getId())));
            }
        }

        if (countryCode != null) {
            countryRequestBody = Helpers.createRequestBodyForString(countryCode);
        }

        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            photo = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }

        DDScannerApplication.getInstance().getDdScannerRestClient().postUpdateDiveCenterProfile(resultListener, photo,  emails, phones, diveSpots, languages, nameRequestBody, countryRequestBody, addressRequestBody, Helpers.createRequestBodyForString("1"));
        materialDialog.show();
    }

    private boolean isDataValid() {
        for (TextView textView : phonesErrors) {
            textView.setVisibility(View.GONE);
        }
        for (TextView textView : emailsErrors) {
            textView.setVisibility(View.GONE);
        }
        boolean isDataValid = true;
        for (EditText editText : phonesEditTexts) {
            if (!validCellPhone(editText.getText().toString())) {
                phonesErrors.get(phonesEditTexts.indexOf(editText)).setVisibility(View.VISIBLE);
                isDataValid = false;
            }
        }
        for (EditText editText : emailsEditTexts) {
            if (!validEmail(editText.getText().toString())) {
                emailsErrors.get(emailsEditTexts.indexOf(editText)).setVisibility(View.VISIBLE);
                isDataValid = false;
            }
        }
        return isDataValid;
    }

    private boolean validCellPhone(String number) {
        return Patterns.PHONE.matcher(number).matches();
    }

    private boolean validEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogHelpers.showDialogAfterChanging(R.string.dialog_leave_title, R.string.dialog_leave_review_message, this, this);
                return true;
            case R.id.logout:
                Intent intent = new Intent();
                intent.putExtra("id", String.valueOf(binding.getDcViewModel().getDiveCenterProfile().getId()));
                setResult(RESULT_CODE_PROFILE_LOGOUT, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        diveSpotsResultListener.setCancelled(true);
        languagesResultListener.setCancelled(true);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }
}
