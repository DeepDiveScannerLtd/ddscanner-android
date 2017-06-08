package com.ddscanner.screens.divespot.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Language;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SpotPhotoEditScreenEntity;
import com.ddscanner.entities.Translation;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.events.ChangeTranslationEvent;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.PickCountryActivity;
import com.ddscanner.ui.activities.PickLanguageActivity;
import com.ddscanner.ui.activities.PickLocationActivity;
import com.ddscanner.ui.activities.SearchSealifeActivity;
import com.ddscanner.ui.adapters.CharacteristicSpinnerItemsAdapter;
import com.ddscanner.ui.adapters.PhotosListAdapterWithCover;
import com.ddscanner.ui.adapters.PhotosListAdapterWithoutCover;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.adapters.TranslationsListAdapter;
import com.ddscanner.ui.dialogs.AddTranslationDialogFragment;
import com.ddscanner.ui.dialogs.UserActionInfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddDiveSpotActivity extends BaseAppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, DialogClosedListener, AddTranslationDialogFragment.TranslationChangedListener, BaseAppCompatActivity.PictureTakenListener, ConfirmationDialogClosedListener {

    private static final String TAG = AddDiveSpotActivity.class.getSimpleName();
    private static final String DIVE_SPOT_NAME_PATTERN = "^[a-zA-Z0-9 ]*$";
    private static final String ARG_LOCATION = "location";

    private LinearLayout btnAddSealife;

    private Toolbar toolbar;
    private LatLng diveSpotLocation;

    private LinearLayout pickLocation;
    private LinearLayout pickCountry;
    private RecyclerView photos_rc;
    private TextView locationTitle;
    private TextView countryTitle;
    private AppCompatSpinner levelAppCompatSpinner;
    private AppCompatSpinner currentsAppCompatSpinner;
    private AppCompatSpinner objectAppCompatSpinner;
    private EditText depth;
    private EditText visibilityMin;
    private EditText visibilityMax;
    private Button btnSave;
    private String createdSpotId;
    private RecyclerView sealifesRc;
    private SealifeListAddingDiveSpotAdapter sealifeListAddingDiveSpotAdapter = null;
    private ScrollView mainLayout;
    private ProgressView progressView;
    private MaterialDialog progressDialogUpload;
    private TextView errorLocation;
    private TextView errorDepth;
    private TextView errorSealife;
    private TextView errorImages;
    private TextView errorVisibilityMin;
    private TextView errorVisibilityMax;
    private TextView errorCurrent;
    private TextView errorLevel;
    private TextView errorObject;
    private TextView errorTranslations;
    private TextView errorVisibility;
    private TextView errorCountry;
    private TextView minVisibilityHint;
    private TextView maxVisibilityHint;
    private int maxPhotos = 3;
    private TextView photos;
    private TextView maps;
    private RecyclerView mapsRecyclerView;
    private LatLngBounds diveSpotLatLngBounds;
    private SwitchCompat isEditSwitch;
    private SwitchCompat isWorkingSwitch;
    private LinearLayout isEditLayout;
    private LinearLayout isWorkingLayout;
    private RelativeLayout addTranslationButton;
    private RecyclerView languagesRecyclerView;
    private TranslationsListAdapter translationsListAdapter = new TranslationsListAdapter();
    private PhotosListAdapterWithCover photosListAdapter;
    private PhotosListAdapterWithoutCover mapsListAdapter;

    private List<String> photoUris = new ArrayList<>();
    private List<String> mapsUris = new ArrayList<>();
    private ArrayList<Language> languagesList = new ArrayList<>();
    private List<SealifeShort> sealifes = new ArrayList<>();
    private Map<String, TextView> errorsMap = new HashMap<>();
    private FiltersResponseEntity filters;
    private boolean isShownMapsPhotos = false;
    private int previousPosition = -1;

    private RequestBody requestIsWorkingHere, requestIsEdit, translations, requestCoverNumber, requestLat, requestLng, requestDepth, requestCurrents, requestLevel, requestObject, requestMinVisibility, requestMaxVisibility, requsetCountryCode = null;
    private List<MultipartBody.Part> sealife = new ArrayList<>();
    private List<MultipartBody.Part> images = new ArrayList<>();
    private List<MultipartBody.Part> mapsList = new ArrayList<>();
    private List<MultipartBody.Part> translationsList = new ArrayList<>();
    private boolean isFromMap;
    private boolean isCountryChosed = false;
    private ArrayList<String> languages = new ArrayList<>();
    private Translation currentTranslation;
    private Map<String, Translation> languagesMap = new HashMap<>();
    private String lastCode;


    private DDScannerRestClient.ResultListener<String> resultListener = new DDScannerRestClient.ResultListener<String>() {
        @Override
        public void onSuccess(String result) {
            progressDialogUpload.dismiss();
            EventsTracker.trackDivespotCreated();
            showSuccessDialog(result);
        }

        @Override
        public void onConnectionFailure() {
            progressDialogUpload.dismiss();
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_CONNECTION_ERROR, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            progressDialogUpload.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logoutFromAllAccounts();
                    LoginActivity.showForResult(AddDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    break;
                case BAD_REQUEST_ERROR_400:
                    Helpers.errorHandling(errorsMap, errorMessage);
                    break;
                case SERVER_INTERNAL_ERROR_500:
                default:
                    UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR, false);
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            progressDialogUpload.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    public static void show(Context context) {
        Intent intent = new Intent(context, AddDiveSpotActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dive_spot);
        EventsTracker.trackDiveSpotCreation();
        isFromMap = getIntent().getBooleanExtra(Constants.ADD_DIVE_SPOT_INTENT_IS_FROM_MAP, false);
        photosListAdapter = new PhotosListAdapterWithCover(this, DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
        mapsListAdapter = new PhotosListAdapterWithoutCover(this);
        findViews();
        setUi();
        requsetCountryCode = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), "RU");
        //  DDScannerApplication.getInstance().getDdScannerRestClient(this).getFilters(filtersResultListener);
        makeErrorsMap();
    }

    private void findViews() {
        isWorkingLayout = (LinearLayout) findViewById(R.id.working_layout);
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
            isWorkingLayout.setVisibility(View.VISIBLE);
        }
        errorVisibility = (TextView) findViewById(R.id.error_visibility);
        maxVisibilityHint = (TextView) findViewById(R.id.max_visibility_hint);
        minVisibilityHint = (TextView) findViewById(R.id.min_visibility_hint);
        isWorkingSwitch = (SwitchCompat) findViewById(R.id.switch_button_working);
        isEditSwitch = (SwitchCompat) findViewById(R.id.switch_button_edit);
        isEditSwitch.setOnCheckedChangeListener(this);
        isWorkingSwitch.setOnCheckedChangeListener(this);
        isEditLayout = (LinearLayout) findViewById(R.id.edit_layout);
        depth = (EditText) findViewById(R.id.depth);
        btnAddSealife = (LinearLayout) findViewById(R.id.btn_add_sealife);
        photos_rc = (RecyclerView) findViewById(R.id.photos_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        levelAppCompatSpinner = (AppCompatSpinner) findViewById(R.id.level_spinner);
        objectAppCompatSpinner = (AppCompatSpinner) findViewById(R.id.object_spinner);
        currentsAppCompatSpinner = (AppCompatSpinner) findViewById(R.id.currents_spinner);
        pickLocation = (LinearLayout) findViewById(R.id.location_layout);
        pickCountry = (LinearLayout) findViewById(R.id.country_layout);
        locationTitle = (TextView) findViewById(R.id.location);
        btnSave = (Button) findViewById(R.id.button_create);
        sealifesRc = (RecyclerView) findViewById(R.id.sealifes_rc);
        mainLayout = (ScrollView) findViewById(R.id.main_layout);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        errorDepth = (TextView) findViewById(R.id.error_depth);
        errorCountry = (TextView) findViewById(R.id.error_country);
        errorLocation = (TextView) findViewById(R.id.error_location);
        errorImages = (TextView) findViewById(R.id.error_images);
        errorSealife = (TextView) findViewById(R.id.error_sealife);
        errorVisibilityMax = (TextView) findViewById(R.id.error_visibility_max);
        errorVisibilityMin = (TextView) findViewById(R.id.error_visibility_min);
        errorCurrent = (TextView) findViewById(R.id.error_current);
        errorTranslations = (TextView) findViewById(R.id.error_translations);
        errorLevel = (TextView) findViewById(R.id.error_level);
        errorObject = (TextView) findViewById(R.id.error_object);
        visibilityMax = (EditText) findViewById(R.id.maxVisibility);
        visibilityMin = (EditText) findViewById(R.id.minVisibility);
        photos = (TextView) findViewById(R.id.photos);
        maps = (TextView) findViewById(R.id.maps);
        mapsRecyclerView = (RecyclerView) findViewById(R.id.maps_rc);
        countryTitle = (TextView) findViewById(R.id.country_title);
        addTranslationButton = (RelativeLayout) findViewById(R.id.button_add_language);
        languagesRecyclerView = (RecyclerView) findViewById(R.id.languages_list);
    }

    private void setUi() {
        progressDialogUpload = Helpers.getMaterialDialog(this);
        pickCountry.setOnClickListener(this);
        ProgressDialog progressDialog = new ProgressDialog(this);
        languagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        languagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        languagesRecyclerView.setAdapter(translationsListAdapter);
        setAppCompatSpinnerValues(currentsAppCompatSpinner, Helpers.getListOfCurrentsTypes(), "Current");
        setAppCompatSpinnerValues(levelAppCompatSpinner, Helpers.getDiveLevelTypes(), "Diver level");
        setAppCompatSpinnerValues(objectAppCompatSpinner, Helpers.getDiveSpotTypes(), "Object");
        btnSave.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);
        maps.setOnClickListener(this);
        addTranslationButton.setOnClickListener(this);
        /* Recycler view with images settings*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
        photos_rc.setAdapter(photosListAdapter);
        LinearLayoutManager mapsLayoutManager = new LinearLayoutManager(AddDiveSpotActivity.this);
        mapsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mapsRecyclerView.setLayoutManager(mapsLayoutManager);
        mapsRecyclerView.setAdapter(mapsListAdapter);

        /* Recycler view with sealifes settings*/
        LinearLayoutManager sealifeLayoutManager = new LinearLayoutManager(
                AddDiveSpotActivity.this);
        sealifeLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sealifesRc.setNestedScrollingEnabled(false);
        sealifesRc.setHasFixedSize(false);
        sealifesRc.setLayoutManager(sealifeLayoutManager);

        /*Toolbar settings*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.new_divespot);

        progressDialog.setCancelable(false);
        progressView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LOCATION:
                if (resultCode == RESULT_OK) {
                    this.diveSpotLocation = data.getParcelableExtra(ARG_LOCATION);
                    locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE:
                Helpers.hideKeyboard(this);
                if (resultCode == RESULT_OK) {
                    SealifeShort sealifeShort = (SealifeShort) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);

                    if (Helpers.checkIsSealifeAlsoInList((ArrayList<SealifeShort>) sealifes, sealifeShort.getId())) {
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.sealife_already_added);
                        return;
                    }
                    sealifes.add(sealifeShort);
                    sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter((ArrayList<SealifeShort>) sealifes, this);
                    sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND:
                if (resultCode == RESULT_OK) {
                    makeAddDiveSpotRequest();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LANGUAGE:
                if (resultCode == RESULT_OK) {
                    Language language = (Language) data.getSerializableExtra("language");
                    for (Translation temp : translationsListAdapter.getTranslations()) {
                        if (temp.getCode().equals(language.getCode())) {
                            return;
                        }
                    }
                    AddTranslationDialogFragment.show(getSupportFragmentManager(), language.getCode(), language.getName(), "", "");
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_COUNTRY:
                if (resultCode == RESULT_OK) {
                    isCountryChosed = true;
                    countryTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
                    BaseIdNamePhotoEntity baseIdNamePhotoEntity = (BaseIdNamePhotoEntity) data.getSerializableExtra("country");
                    countryTitle.setText(baseIdNamePhotoEntity.getName());
                    requsetCountryCode = Helpers.createRequestBodyForString(baseIdNamePhotoEntity.getCode());
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_PICK_LOCATION_ACTIVITY_PLACE_AUTOCOMPLETE:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    diveSpotLocation = place.getLatLng();
                    diveSpotLatLngBounds = place.getViewport();
                    if (place.getAddress() != null) {
                        locationTitle.setText(place.getAddress().toString());
                    }
                    locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
                }
                break;
        }
    }

    private void setAppCompatSpinnerValues(AppCompatSpinner spinner, List<String> values, String tag) {
        ArrayList<String> objects = new ArrayList<String>();
        objects.add(tag);
        objects.addAll(values);
        ArrayAdapter<String> adapter = new CharacteristicSpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
    }

    private void makeAddDiveSpotRequest() {
        progressDialogUpload.show();
        //   DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddDiveSpot(addDiveSpotResultListener, sealife, images, requestName, requestLat, requestLng, requestDepth, requestMinVisibility, requestMaxVisibility, requestCurrents, requestLevel, requestObject, requestDescription, requestToken, requestSocial, requestSecret);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postAddDiveSpot(resultListener, sealife, images, mapsList, requestLat, requestLng, requsetCountryCode, requestDepth, requestLevel, requestCurrents, requestMinVisibility, requestMaxVisibility, requestCoverNumber, translations, requestObject, requestIsEdit, requestIsWorkingHere);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_layout:
                PickLocationActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LOCATION, diveSpotLocation);
                break;
            case R.id.btn_add_sealife:
                SearchSealifeActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE, diveSpotLocation);
                break;
            case R.id.button_create:
//                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
                createRequestBodyies();
//                } else {
//                    Intent loginIntent = new Intent(this, SocialNetworks.class);
//                    startActivityForResult(loginIntent, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN);
//                }
                break;
            case R.id.photos:
                changeViewState(photos, maps);
                photos_rc.setVisibility(View.VISIBLE);
                mapsRecyclerView.setVisibility(View.GONE);
                break;
            case R.id.maps:
                changeViewState(maps, photos);
                mapsRecyclerView.setVisibility(View.VISIBLE);
                photos_rc.setVisibility(View.GONE);
                break;
            case R.id.country_layout:
                PickCountryActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_COUNTRY);
                break;
            case R.id.button_add_language:
                PickLanguageActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LANGUAGE);
                break;
        }
    }

    private void changeViewState(TextView activeTextView, TextView disableTextView) {
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        activeTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_rectangle));
        activeTextView.setOnClickListener(null);

        disableTextView.setTextColor(ContextCompat.getColor(this, R.color.diactive_button_photo_color));
        disableTextView.setBackground(null);
        disableTextView.setOnClickListener(this);
    }

    private boolean isSomethingEntered() {
        if (diveSpotLocation != null || isCountryChosed || !depth.getText().toString().isEmpty() || languagesList.size() > 0 || photosListAdapter.getItemCount() > 1 || mapsListAdapter.getItemCount() > 1 || !visibilityMin.getText().toString().isEmpty() || !visibilityMax.getText().toString().isEmpty() || currentsAppCompatSpinner.getSelectedItemPosition() != 0 || levelAppCompatSpinner.getSelectedItemPosition() != 0 || objectAppCompatSpinner.getSelectedItemPosition() != 0 || sealifes.size() > 0) {
            return true;
        }
        return false;
    }

    private void createRequestBodyies() {
        hideErrorsFields();
        if (validatefields()) {
            return;
        }
        requestDepth = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                depth.getText().toString().trim());
        if (diveSpotLocation != null) {
            requestLat = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    String.valueOf(diveSpotLocation.latitude));
            requestLng = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    String.valueOf(diveSpotLocation.longitude));
        }
        if (isWorkingSwitch.isChecked()) {
            requestIsWorkingHere = Helpers.createRequestBodyForString("1");
            if (isEditSwitch.isChecked()) {
                requestIsEdit = Helpers.createRequestBodyForString("1");
            } else {
                requestIsEdit = Helpers.createRequestBodyForString("0");
            }

        } else {
            requestIsWorkingHere = Helpers.createRequestBodyForString("0");
            requestIsEdit = Helpers.createRequestBodyForString("0");
        }
        translations = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), new Gson().toJson(translationsListAdapter.getTranslations()));
        requestCoverNumber = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(photosListAdapter.getCoverPhotoPositionForAddDiveSpot()));
        requestObject = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getDiveSpotTypes().indexOf(objectAppCompatSpinner.getSelectedItem().toString()) + 1));
        requestCurrents = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getListOfCurrentsTypes().indexOf(currentsAppCompatSpinner.getSelectedItem().toString()) + 1));
        requestLevel = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getDiveLevelTypes().indexOf(levelAppCompatSpinner.getSelectedItem().toString()) + 1));
        requestMinVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMin.getText().toString());
        requestMaxVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMax.getText().toString());
        sealife = new ArrayList<>();
        if (sealifeListAddingDiveSpotAdapter != null && sealifeListAddingDiveSpotAdapter.getSealifes() != null) {
            sealifes = sealifeListAddingDiveSpotAdapter.getSealifes();
        } else {
            sealife = null;
        }
        if (sealife != null && sealifes.size() > 0) {
            for (int i = 0; i < sealifes.size(); i++) {
                sealife.add(MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE_ARRAY, sealifes.get(i).getId()));
            }
        }
        if (photosListAdapter.getNewPhotos().size() > 0) {
            images = new ArrayList<>();
            for (int i = 0; i < photosListAdapter.getNewPhotos().size(); i++) {
                File image = new File(photosListAdapter.getNewPhotos().get(i).getPhotoPath());
                image = Helpers.compressFile(image, this);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY, image.getName(), requestFile);
                images.add(part);
            }
        } else {
            images = null;
        }
        if (mapsListAdapter.getNewPhotos().size() > 0) {
            mapsList = new ArrayList<>();
            for (int i = 0; i < mapsListAdapter.getNewPhotos().size(); i++) {
                File image = new File(mapsListAdapter.getNewPhotos().get(i));
                image = Helpers.compressFile(image, this);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_MAPS_ARRAY, image.getName(), requestFile);
                mapsList.add(part);
            }
        } else {
            mapsList = null;
        }
        makeAddDiveSpotRequest();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isSomethingEntered()) {
                    DialogHelpers.showDialogAfterChangesInActivity(getSupportFragmentManager());
                } else {
                    finish();
                }
                // onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideErrorsFields() {
        for (Map.Entry<String, TextView> entry : errorsMap.entrySet()) {
            entry.getValue().setVisibility(View.GONE);
        }
        errorDepth.setText(R.string.depth_required);
        errorVisibility.setText(R.string.visibility_rquired);
        errorVisibility.setVisibility(View.GONE);
        minVisibilityHint.setVisibility(View.VISIBLE);
        maxVisibilityHint.setVisibility(View.VISIBLE);
    }

    private void makeErrorsMap() {
        errorsMap.put("depth", errorDepth);
        errorsMap.put("location", errorLocation);
        errorsMap.put("photos", errorImages);
        errorsMap.put("sealife", errorSealife);
        errorsMap.put("visibility_min", errorVisibilityMin);
        errorsMap.put("visibility_max", errorVisibilityMax);
        errorsMap.put("diving_skill", errorLevel);
        errorsMap.put("dive_spot_type", errorObject);
        errorsMap.put("currents", errorCurrent);
        errorsMap.put("", errorVisibility);
        errorsMap.put("", errorCountry);
        errorsMap.put("", errorTranslations);
        errorsMap.put("", errorCountry);
    }

    private boolean validatefields() {
        boolean isSomethingWrong = false;
        int downestCoordinate = 0;


        if (sealifeListAddingDiveSpotAdapter == null || sealifeListAddingDiveSpotAdapter.getSealifes() == null || sealifeListAddingDiveSpotAdapter.getSealifes().size() < 1) {
            isSomethingWrong = true;
            errorSealife.setVisibility(View.VISIBLE);
            downestCoordinate = sealifesRc.getBottom();
        }

        if (objectAppCompatSpinner.getSelectedItemPosition() == 0) {
            isSomethingWrong = true;
            errorObject.setVisibility(View.VISIBLE);
            downestCoordinate = objectAppCompatSpinner.getBottom();
        }

        if (levelAppCompatSpinner.getSelectedItemPosition() == 0) {
            isSomethingWrong = true;
            errorLevel.setVisibility(View.VISIBLE);
            downestCoordinate = levelAppCompatSpinner.getBottom();
        }

        if (currentsAppCompatSpinner.getSelectedItemPosition() == 0) {
            isSomethingWrong = true;
            errorCurrent.setVisibility(View.VISIBLE);
            downestCoordinate = currentsAppCompatSpinner.getBottom();
        }

        if (visibilityMin.getText().toString().isEmpty() || visibilityMax.getText().toString().isEmpty()) {
            isSomethingWrong = true;
            errorVisibility.setVisibility(View.VISIBLE);
            minVisibilityHint.setVisibility(View.GONE);
            maxVisibilityHint.setVisibility(View.GONE);
            downestCoordinate = visibilityMax.getBottom();
        } else {
            downestCoordinate = visibilityMax.getBottom();
            if (Float.parseFloat(visibilityMax.getText().toString()) <= Float.parseFloat(visibilityMin.getText().toString())) {
                isSomethingWrong = true;
                errorVisibility.setVisibility(View.VISIBLE);
                errorVisibility.setText(R.string.error_visivibility_append);
                minVisibilityHint.setVisibility(View.GONE);
                maxVisibilityHint.setVisibility(View.GONE);
            }
            if (visibilityMin.getText().toString().isEmpty() || Float.parseFloat(visibilityMin.getText().toString()) < 1 || Float.parseFloat(visibilityMin.getText().toString()) > 100) {
                isSomethingWrong = true;
                errorVisibility.setVisibility(View.GONE);
                errorVisibilityMin.setVisibility(View.VISIBLE);
                minVisibilityHint.setVisibility(View.GONE);
            }

            if (visibilityMax.getText().toString().isEmpty() || Float.parseFloat(visibilityMax.getText().toString()) < 1 || Float.parseFloat(visibilityMax.getText().toString()) > 100) {
                isSomethingWrong = true;
                errorVisibility.setVisibility(View.GONE);
                errorVisibilityMax.setVisibility(View.VISIBLE);
                maxVisibilityHint.setVisibility(View.GONE);
            }

        }

        if (depth.getText().toString().isEmpty()) {
            isSomethingWrong = true;
            errorDepth.setVisibility(View.VISIBLE);
            downestCoordinate = depth.getBottom();
        } else {
            downestCoordinate = depth.getBottom();
            if (Float.parseFloat(depth.getText().toString()) < 5 || Float.parseFloat(depth.getText().toString()) > 1092) {
                isSomethingWrong = true;
                errorDepth.setText(R.string.depth_vlue);
                errorDepth.setVisibility(View.VISIBLE);
            }
        }


        if (diveSpotLocation == null) {
            isSomethingWrong = true;
            errorLocation.setVisibility(View.VISIBLE);
            downestCoordinate = locationTitle.getBottom();
        }

        if (!isCountryChosed) {
            isSomethingWrong = true;
            errorCountry.setVisibility(View.VISIBLE);
            downestCoordinate = countryTitle.getBottom();
        }

        if (translationsListAdapter.getTranslations().size() < 1) {
            isSomethingWrong = true;
            errorTranslations.setVisibility(View.VISIBLE);
            downestCoordinate = addTranslationButton.getBottom();
        }

        if (photosListAdapter.getNewPhotos().size() < 1) {
            isSomethingWrong = true;
            errorImages.setVisibility(View.VISIBLE);
            downestCoordinate = errorImages.getBottom();
        }

        if (isSomethingWrong) {
            scrollToError(downestCoordinate);
        }

        return isSomethingWrong;

    }

    private void scrollToError(final int bottom) {
        new Handler().post(() -> mainLayout.scrollTo(0, bottom));
    }

    @Override
    protected void onPause() {
        super.onPause();
        DDScannerApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DDScannerApplication.activityResumed();
        if (!Helpers.hasConnection(this)) {
            DDScannerApplication.showErrorActivity(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        DDScannerApplication.bus.unregister(this);
    }

    private void showSuccessDialog(final String diveSpotId) {
        this.createdSpotId = diveSpotId;
        if (!isFromMap) {
            DiveSpotDetailsActivity.show(AddDiveSpotActivity.this, createdSpotId, EventsTracker.SpotViewSource.UNKNOWN);
            finish();
        } else {
            Intent intent = new Intent();
            LatLng latLng = new LatLng(diveSpotLocation.latitude, diveSpotLocation.longitude);
            intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_RESULT_LAT_LNG, latLng);
            intent.putExtra(Constants.ADD_DIVE_SPOT_INTENT_DIVESPOT_ID, createdSpotId);
            setResult(RESULT_OK, intent);
            finish();
        }
//        UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.thank_you_title, R.string.success_added, DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_DIVE_SPOT_CREATED, false);
    }

    public static void showForResult(Activity context, int requestCode, boolean isFromMap) {
        Intent intent = new Intent(context, AddDiveSpotActivity.class);
        intent.putExtra(Constants.ADD_DIVE_SPOT_INTENT_IS_FROM_MAP, isFromMap);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_CONNECTION_ERROR:
            case DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR:
                finish();
                break;
        }
    }

    @Subscribe
    public void pickPhotoFrom(AddPhotoDoListEvent event) {
        pickPhotosFromGallery();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_button_edit:
                break;
            case R.id.switch_button_working:
                if (b) {
                    isEditLayout.setVisibility(View.VISIBLE);
                    break;
                }
                isEditLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onTranslationChanged(Translation translation) {
        translationsListAdapter.add(translation);
    }

    @Subscribe
    public void changeTranslation(ChangeTranslationEvent event) {
        AddTranslationDialogFragment.show(getSupportFragmentManager(), event.getTranslation().getCode(), event.getTranslation().getLanguage(), event.getTranslation().getName(), event.getTranslation().getDescription());
    }

    @Override
    public void onPicturesTaken(ArrayList<String> pictures) {
        ArrayList<SpotPhotoEditScreenEntity> photos = new ArrayList<>();
        SpotPhotoEditScreenEntity photo;
        for (String path : pictures) {
            photo = new SpotPhotoEditScreenEntity();
            photo.setCover(false);
            photo.setAuthorId(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
            photo.setPhotoPath(path);
            photos.add(photo);
        }
        if (photos_rc.getVisibility() == View.VISIBLE) {
            photosListAdapter.addDevicePhotos(photos);
        } else {
            mapsListAdapter.addDevicePhotos(pictures);
        }
    }

    @Override
    public void onPictureFromCameraTaken(File picture) {

    }

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        finish();
    }

}
