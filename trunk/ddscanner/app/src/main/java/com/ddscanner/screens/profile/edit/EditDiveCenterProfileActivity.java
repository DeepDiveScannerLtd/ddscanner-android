package com.ddscanner.screens.profile.edit;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.databinding.EditDcProfileViewBinding;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.CountryEntity;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.Language;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.interfaces.RemoveLayoutClickListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.brands.BrandsActivity;
import com.ddscanner.screens.photo.crop.CropImageActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.BaseSearchActivity;
import com.ddscanner.ui.activities.ChangeAddressActivity;
import com.ddscanner.ui.activities.PickLanguageActivity;
import com.ddscanner.ui.activities.SearchSpotOrLocationActivity;
import com.ddscanner.ui.adapters.BaseAdapterForEditProfile;
import com.ddscanner.ui.adapters.DiveCenterLanguagesListAdapter;
import com.ddscanner.ui.adapters.DiveSpotsListForEditDcAdapter;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.ui.views.EmailInputView;
import com.ddscanner.ui.views.PhoneInputView;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.ItemDecoration;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class EditDiveCenterProfileActivity extends BaseAppCompatActivity implements BaseAppCompatActivity.PictureTakenListener, DialogClosedListener, ConfirmationDialogClosedListener {

    private static final String COMPANY_BUTTON_TAG= "company_button_tag";
    private static final String RESELLER_BUTTON_TAG= "reseller_button_tag";
    private static final String ARG_DIVECENTER = "divecenter";
    private static final String ARG_ISSPOTS = "isspots";
    private static final String ARG_ISREGISTRTION = "is_registration";
    private static final String ARG_ISLOGOUTABLE = "islogoutable";
    private static final String ARG_COUNTRY = "country";
    private static final String ARG_ADDRESS = "address";

    private List<MultipartBody.Part> emails =  new ArrayList<>();
    private List<MultipartBody.Part> phones = new ArrayList<>();
    private List<MultipartBody.Part> languages = null;
    private List<MultipartBody.Part> diveSpots = null;
    private List<MultipartBody.Part> brands = null;
    private List<MultipartBody.Part> associations = null;
    private EditText countryEdiText;
    private EditText addressEditText;
    private String imagePath;
    private MultipartBody.Part photo = null;
    private String countryCode = null;
    private String locationLatitude = null;
    private String locationLongitude = null;
    private RequestBody countryRequestBody = null, addressRequestBody = null, nameRequestBody = null, isDiveShopRequestBody = null, aboutRequestBody = null;
    private DiveSpotsListForEditDcAdapter diveSpotsListForEditDcAdapter = new DiveSpotsListForEditDcAdapter();
    private DiveCenterLanguagesListAdapter languagesListAdapter = new DiveCenterLanguagesListAdapter();
    private BaseAdapterForEditProfile associationsListAdapter = new BaseAdapterForEditProfile();
    private BaseAdapterForEditProfile brandsListAdapter = new BaseAdapterForEditProfile();
    private String country;
    private MaterialDialog materialDialog;
    private boolean isHaveSpots;
    private boolean isLanguagesDownloaded = false;
    private boolean isDiveSpotsDownloaded = false;
    private ColorStateList colorStateList;
    private TextView editAddress;
    private ChipsLayoutManager diveSpotsLayoutManager;
    private ChipsLayoutManager languagesLayoutManager;
    private ChipsLayoutManager associationsLayoutManager;
    private ChipsLayoutManager brandsLayoutManager;
    private RemoveLayoutClickListener removePhoneLayoutClickListener;
    private RemoveLayoutClickListener removeEmailLayoutClickListener;

    private EditDcProfileViewBinding binding;
    private Gson gson = new Gson();

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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            isDiveSpotsDownloaded = true;
            dismissMaterialDiaog();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            isDiveSpotsDownloaded = true;
            dismissMaterialDiaog();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
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
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            isLanguagesDownloaded = true;
            dismissMaterialDiaog();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
            Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
        }

        @Override
        public void onInternetConnectionClosed() {
            isLanguagesDownloaded = true;
            dismissMaterialDiaog();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDIT_DC_ACTIVITY_HIDE, false);
        }

    };

    private DDScannerRestClient.ResultListener<Void> resultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            materialDialog.dismiss();
            setResult(RESULT_OK);
            EventsTracker.trackProfileEdited();
            DDScannerApplication.getInstance().getSharedPreferenceHelper().setIsNeedContinueRegistration(false);
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

        }

        @Override
        public void onInternetConnectionClosed() {
            materialDialog.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
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
        intent.putExtra(ARG_DIVECENTER, diveCenterString);
        intent.putExtra(ARG_ISSPOTS, isHaveSpots);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.edit_dc_profile_view);
        binding.setHandlers(this);
        binding.setDcViewModel(new EditDiveCenterProfileActivityViewModel(new Gson().fromJson(getIntent().getStringExtra(ARG_DIVECENTER), DiveCenterProfile.class)));
        initLayoutManagers();
        initCheckbox();
        initListeners();
        isHaveSpots = getIntent().getBooleanExtra(ARG_ISSPOTS, false);
        countryCode = binding.getDcViewModel().getDiveCenterProfile().getCountryCode();
        binding.name.setText(binding.getDcViewModel().getDiveCenterProfile().getName());
        setupToolbar(R.string.edit_profile_activity, R.id.toolbar, R.menu.menu_search_save);
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsNeedToContinueRegistration()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        setupUi();

    }

    private void initCheckbox() {
        binding.diveShopCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.brandsBlock.setVisibility(View.VISIBLE);
            } else {
                binding.brandsBlock.setVisibility(View.GONE);
            }
        });
    }

    private void initListeners() {
        removePhoneLayoutClickListener = view -> binding.phones.removeView(view);
        removeEmailLayoutClickListener = view -> binding.emails.removeView(view);
    }

    private void initLayoutManagers() {
        diveSpotsLayoutManager = ChipsLayoutManager.newBuilder(this).setOrientation(ChipsLayoutManager.HORIZONTAL).build();
        languagesLayoutManager = ChipsLayoutManager.newBuilder(this).setOrientation(ChipsLayoutManager.HORIZONTAL).build();
        associationsLayoutManager = ChipsLayoutManager.newBuilder(this).setOrientation(ChipsLayoutManager.HORIZONTAL).build();
        brandsLayoutManager = ChipsLayoutManager.newBuilder(this).setOrientation(ChipsLayoutManager.HORIZONTAL).build();
    }

    private void initLists() {
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(Helpers.convertDpToIntPixels(4, this), Helpers.convertDpToIntPixels(4, this));

        binding.diveSpotsList.setLayoutManager(diveSpotsLayoutManager);
        binding.diveSpotsList.setAdapter(diveSpotsListForEditDcAdapter);
        binding.diveSpotsList.addItemDecoration(itemDecoration);

        binding.languagesList.setLayoutManager(languagesLayoutManager);
        binding.languagesList.setAdapter(languagesListAdapter);
        binding.languagesList.addItemDecoration(itemDecoration);

        binding.associationsList.setLayoutManager(associationsLayoutManager);
        binding.associationsList.setAdapter(associationsListAdapter);
        binding.associationsList.addItemDecoration(itemDecoration);

        binding.brandsList.setLayoutManager(brandsLayoutManager);
        binding.brandsList.setAdapter(brandsListAdapter);
        binding.brandsList.addItemDecoration(itemDecoration);

        binding.diveSpotsList.setNestedScrollingEnabled(false);
        binding.languagesList.setNestedScrollingEnabled(false);
        binding.associationsList.setNestedScrollingEnabled(false);
        binding.brandsList.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setupUi() {
        materialDialog = Helpers.getMaterialDialog(this);
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsNeedToContinueRegistration()) {
            materialDialog.show();
            if (isHaveSpots) {
                DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterDiveSpotsList(diveSpotsResultListener, binding.getDcViewModel().getDiveCenterProfile().getId().toString());
            } else {
                isDiveSpotsDownloaded = true;
            }
            DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveCenterLanguages(languagesResultListener, String.valueOf(binding.getDcViewModel().getDiveCenterProfile().getId()));
        }
        initLists();
        if (binding.getDcViewModel().getDiveCenterProfile().getWorkingSpots() != null) {
            diveSpotsListForEditDcAdapter.addAllDiveSpots(binding.getDcViewModel().getDiveCenterProfile().getWorkingSpots());
        }
        if (binding.getDcViewModel().getDiveCenterProfile().getAbout() != null) {
            binding.bio.setText(binding.getDcViewModel().getDiveCenterProfile().getAbout());
        }
        if (binding.getDcViewModel().getDiveCenterProfile().getAssociations() != null) {
            associationsListAdapter.addAllobjects(binding.getDcViewModel().getDiveCenterProfile().getAssociatinsForEditProfile());
        }
        if (binding.getDcViewModel().getDiveCenterProfile().getBrands() != null) {
            brandsListAdapter.addAllobjects(binding.getDcViewModel().getDiveCenterProfile().getBrandsForEditProfile());
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
            if (binding.getDcViewModel().getDiveCenterProfile().getCountry() != null) {
                addAddressesView(binding.getDcViewModel().getDiveCenterProfile().getAddresses().get(0).getName(), binding.getDcViewModel().getDiveCenterProfile().getCountry().getName());
            } else {
                addAddressesView(binding.getDcViewModel().getDiveCenterProfile().getAddresses().get(0).getName(), "");
            }
            binding.chooseAddress.setText(R.string.edit);
        }
        binding.diveShopCheckBox.setChecked(binding.getDcViewModel().getDiveCenterProfile().isDiveShop());
    }

    public void addEmailClicked(View view) {
        EmailInputView emailInputView = new EmailInputView(this);
        emailInputView.setRemoveLayoutClickListener(removeEmailLayoutClickListener);
        binding.emails.addView(emailInputView);
    }

    public void addPhoneClicked(View view) {
        PhoneInputView phoneInputView = new PhoneInputView(this);
        phoneInputView.showDeleteButon();
        phoneInputView.setRemoveLayoutClickListener(removePhoneLayoutClickListener);
        binding.phones.addView(phoneInputView);
    }

    private void addEmailView(String text) {
        EmailInputView emailInputView = new EmailInputView(this);
        emailInputView.setRemoveLayoutClickListener(removeEmailLayoutClickListener);
        emailInputView.setText(text);
        binding.emails.addView(emailInputView);
    }

    private void addPhoneView(String text) {
        PhoneInputView phoneInputView = new PhoneInputView(this);
        phoneInputView.showDeleteButon();
        phoneInputView.setRemoveLayoutClickListener(removePhoneLayoutClickListener);
        phoneInputView.setPhone(text);
        binding.phones.addView(phoneInputView);
    }

    public void addDiveSpotClicked(View view) {
        SearchSpotOrLocationActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_SPOT, true);
    }

    public void chooseAddressClicked(View view) {
        if (binding.getDcViewModel().getDiveCenterProfile().getAddresses() != null) {
            ChangeAddressActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS, new Gson().toJson(binding.getDcViewModel().getDiveCenterProfile().getCountry()), new Gson().toJson(binding.getDcViewModel().getDiveCenterProfile().getAddresses().get(0)));
        } else {
            ChangeAddressActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS, null, null);
        }
    }

    public void pickCountryClicked(View view) {
    }

    public void changePhotoButtonCLicked(View view) {
//        pickCroppedImage(CropImageActivity.CropImageSource.DIVE_CENTER_PROFILE);
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
        Picasso.with(this).load("file://" + pictures.get(0)).resize(Math.round(Helpers.convertDpToPixel(65, this)),Math.round(Helpers.convertDpToPixel(65, this))).centerCrop().transform(new RoundedCornersTransformation(Math.round(Helpers.convertDpToPixel(2, this)),0, RoundedCornersTransformation.CornerType.ALL)).into(binding.logo);
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
                    DiveSpotShort diveSpotShort = (DiveSpotShort) data.getSerializableExtra("divespot");
                    if (diveSpotsListForEditDcAdapter.getObjects().size() > 0) {
                        for (DiveSpotShort spot : diveSpotsListForEditDcAdapter.getObjects()) {
                            if (spot.getId() == diveSpotShort.getId()) {
                                return;
                            }
                        }
                    }
                    diveSpotsListForEditDcAdapter.addDiveSpot(diveSpotShort);
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
                        if (place.getAddress() != null) {
                            addAddressesView(place.getAddress().toString(), addresses.get(0).getCountryName());
                            countryCode = addresses.get(0).getCountryCode();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.empty_string, R.string.please_choose_right_loction, false);
                    }
                }
                break;
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_CHOOSE_ADDRESS:
                if (resultCode == RESULT_OK) {
                    CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra(ARG_COUNTRY);
                    com.ddscanner.entities.Address address = (com.ddscanner.entities.Address) data.getSerializableExtra(ARG_ADDRESS);
                    ArrayList<com.ddscanner.entities.Address> addresses= new ArrayList<>();
                    addresses.add(address);
                    binding.getDcViewModel().getDiveCenterProfile().setCountry(countryEntity);
                    binding.getDcViewModel().getDiveCenterProfile().setAddresses(addresses);
                    countryCode = countryEntity.getCode();
                    locationLatitude = address.getLat().toString();
                    locationLongitude = address.getLng().toString();
                    addAddressesView(binding.getDcViewModel().getDiveCenterProfile().getAddresses().get(0).getName(), binding.getDcViewModel().getDiveCenterProfile().getCountry().getName());
                }
                break;
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_LANGUAGE:
                if (resultCode == RESULT_OK) {
                    Language language = (Language) data.getSerializableExtra("language");
                    if (languagesListAdapter.getObjects().size() > 0) {
                        for (Language current : languagesListAdapter.getObjects()) {
                            if (current.getCode().equals(language.getCode())) {
                                return;
                            }
                        }
                    }
                    languagesListAdapter.addLanguage(language);
                }
                break;
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_ASSOCIATION:
                if (resultCode == RESULT_OK) {
                    String listString = data.getStringExtra("objects");
                    Type listType = new TypeToken<ArrayList<BaseIdNamePhotoEntity>>(){}.getType();
                    ArrayList<BaseIdNamePhotoEntity> baseIdNamePhotoEntities = new Gson().fromJson(listString, listType);
                    associationsListAdapter.updateData(baseIdNamePhotoEntities);
                }
                break;
            case ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_BRAND:
                if (resultCode == RESULT_OK) {
                    String object = data.getStringExtra("brand");
                    brandsListAdapter.addObject(gson.fromJson(object, BaseIdNamePhotoEntity.class));
                }
                break;
        }
    }

    public void addLanguageClicked(View view) {
        PickLanguageActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_LANGUAGE);
    }



    private void addAddressesView(String address, String country) {
        binding.addressInput.disable();
        binding.addressInput.setVisibility(View.VISIBLE);
        binding.chooseAddress.setText(R.string.edit);
        if (!country.isEmpty()) {
            binding.addressInput.setText(country + ", " + address);
        } else {
            binding.addressInput.setText(address);
        }
    }

    public void changePassword(View view) {
        ChangePasswordActivity.show(this);
    }

    private <T> ArrayList<T> getInputViewsList(LinearLayout linearLayout) {
        ArrayList<T> phoneInputViews = new ArrayList<>();
        if (linearLayout.getChildCount() > 0) {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                phoneInputViews.add((T) linearLayout.getChildAt(i));
            }
        }
        return phoneInputViews;
    }

    public void saveChangesClicked(View view) {
        ArrayList<PhoneInputView> phoneInputViews = getInputViewsList(binding.phones);
        ArrayList<EmailInputView> emailInputViews = getInputViewsList(binding.emails);
        if (!isDataValid(phoneInputViews, emailInputViews)) {
            return;
        }
        if (!binding.name.getText().isEmpty()) {
            nameRequestBody = Helpers.createRequestBodyForString(binding.name.getText());
        }

        for (PhoneInputView editText : phoneInputViews) {
            if (!editText.getPhoneWithPlus().trim().isEmpty()) {
                phones.add(MultipartBody.Part.createFormData("phones[]", editText.getPhoneWithPlus().trim()));
            }
        }

        for (EmailInputView emailInputView : emailInputViews) {
            if (!emailInputView.getText().trim().isEmpty()) {
                emails.add(MultipartBody.Part.createFormData("emails[]", emailInputView.getText().trim()));
            }
        }

        if (locationLatitude != null && locationLongitude != null) {
            com.ddscanner.entities.Address address = new com.ddscanner.entities.Address(binding.addressInput.getText(), Double.valueOf(locationLatitude), Double.valueOf(locationLongitude));
            ArrayList<com.ddscanner.entities.Address> addresses = new ArrayList<>();
            addresses.add(address);
            addressRequestBody = Helpers.createRequestBodyForString(new Gson().toJson(addresses));
        }

        if (languagesListAdapter.getObjects().size() > 0) {
            languages = new ArrayList<>();
            for (Language language : languagesListAdapter.getObjects()) {
                languages.add(MultipartBody.Part.createFormData("lang_codes[]", language.getCode()));
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
            file = Helpers.compressFile(file, this);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            photo = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        }

        if (binding.diveShopCheckBox.isChecked()) {
            isDiveShopRequestBody = Helpers.createRequestBodyForString("1");
            if (brandsListAdapter.getItemCount() > 0) {
                brands = new ArrayList<>();
                for (BaseIdNamePhotoEntity baseIdNamePhotoEntity : brandsListAdapter.getOjects()) {
                    brands.add(MultipartBody.Part.createFormData("brands[]", baseIdNamePhotoEntity.getId()));
                }
            }
        } else {
            isDiveShopRequestBody = Helpers.createRequestBodyForString("0");
        }

        if (associationsListAdapter.getItemCount() > 0) {
            associations = new ArrayList<>();
            for (BaseIdNamePhotoEntity baseIdNamePhotoEntity : associationsListAdapter.getOjects()) {
                associations.add(MultipartBody.Part.createFormData("associations[]", baseIdNamePhotoEntity.getId()));
            }
        }

        if (binding.bio.getText().length() > 0) {
            aboutRequestBody = Helpers.createRequestBodyForString(binding.bio.getText());
        }

        DDScannerApplication.getInstance().getDdScannerRestClient(this).postUpdateDiveCenterProfile(resultListener, photo,  emails, phones, diveSpots, languages, associations, brands, nameRequestBody, countryRequestBody, addressRequestBody, isDiveShopRequestBody, aboutRequestBody);
        materialDialog.show();
    }

    private boolean isDataValid(ArrayList<PhoneInputView> phoneInputViews, ArrayList<EmailInputView> emailInputViews) {
        boolean isDataValid = true;
        binding.name.hideError();
        binding.diveSpotError.setVisibility(View.GONE);
        binding.addressError.setVisibility(View.GONE);
        for (PhoneInputView phoneInputView : phoneInputViews) {
            if (!validCellPhone(phoneInputView.getPhoneWithPlus().trim(), phoneInputView.getCountryName())) {
                phoneInputView.setError();
                isDataValid = false;
            } else {
                phoneInputView.hideError();
            }
        }
        for (EmailInputView emailInputView : emailInputViews) {
            if (!validEmail(emailInputView.getText().trim()) && !emailInputView.getText().trim().isEmpty()) {
                emailInputView.showError();
                isDataValid = false;
            } else {
                emailInputView.hideError();
            }
        }

        if (binding.name.getText().isEmpty()) {
            binding.name.setError(getString(R.string.name_is_required_sealife));
            isDataValid = false;
        }

        if (diveSpotsListForEditDcAdapter.getItemCount() == 0) {
            isDataValid = false;
            binding.diveSpotError.setVisibility(View.VISIBLE);
        }

        if (locationLatitude == null || locationLongitude == null) {
            binding.addressError.setVisibility(View.VISIBLE);
            isDataValid = false;
        }

        if (!isDataValid) {
            binding.mainLayout.scrollTo(0,0);
        }



        return isDataValid;
    }

    private boolean validCellPhone(String number, String coutryCode) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        try {
            return util.isValidNumber(util.parse(number, coutryCode));
        } catch (NumberParseException exception) {
            return false;
        }
    }

    private boolean validEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogHelpers.showDialogAfterChangesInActivity(getSupportFragmentManager());
                return true;
            case R.id.save:
                saveChangesClicked(null);
                return true;
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

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        finish();
    }

    public void addAssociationClicked(View view) {
        BaseSearchActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_ASSOCIATION, BaseSearchActivity.SearchSource.ASSOCIATION, gson.toJson(associationsListAdapter.getOjects()));
    }

    public void addBrandClicked(View view) {
        BrandsActivity.showForResult(this, ActivitiesRequestCodes.EDIT_DIVE_CENTER_ACTIVITY_ADD_BRAND);
    }

}
