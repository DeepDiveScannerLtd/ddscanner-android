package com.ddscanner.screens.divespot.edit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
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
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Language;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SpotPhotoEditScreenEntity;
import com.ddscanner.entities.Translation;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.events.AddTranslationClickedEvent;
import com.ddscanner.events.ChangeTranslationEvent;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;
import com.ddscanner.interfaces.DialogClosedListener;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.BaseAppCompatActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.PickCountryActivity;
import com.ddscanner.ui.activities.PickLanguageActivity;
import com.ddscanner.ui.activities.PickLocationActivity;
import com.ddscanner.ui.activities.SearchSealifeActivity;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
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

public class EditDiveSpotActivity extends BaseAppCompatActivity implements BaseAppCompatActivity.PictureTakenListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, DialogClosedListener, AddTranslationDialogFragment.TranslationChangedListener, ConfirmationDialogClosedListener {

    private static final String TAG = EditDiveSpotActivity.class.getSimpleName();
    private static final String ARG_LOCATION = "location";
    private PhotosListAdapterWithCover photosListAdapter = new PhotosListAdapterWithCover(EditDiveSpotActivity.this, DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId());
    private PhotosListAdapterWithoutCover mapsListAdapter = new PhotosListAdapterWithoutCover(EditDiveSpotActivity.this);
    private LinearLayout btnAddSealife;

    private LatLng diveSpotLocation;

    private LinearLayout pickLocation;
    private RecyclerView diveSpotPhotosRecyclrView;
    private TextView locationTitle;
    private TextView countryTitle;
    private AppCompatSpinner levelAppCompatSpinner;
    private AppCompatSpinner currentsAppCompatSpinner;
    private AppCompatSpinner objectAppCompatSpinner;
    private EditText depth;
    private EditText visibilityMin;
    private EditText visibilityMax;
    private EditText description;
    private Button btnSave;
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
    private TextView errorTranslations;
    private TextView errorVisibility;
    private TextView errorCountry;
    private TextView errorCurrent;
    private TextView errorLevel;
    private TextView errorObject;
    private TextView minVisibilityHint;
    private TextView maxVisibilityHint;
    private TextView photos;
    private TextView maps;
    private RecyclerView mapsRecyclerView;
    private SwitchCompat isEditSwitch;
    private SwitchCompat isWorkingSwitch;
    private LinearLayout isEditLayout;
    private LinearLayout workingLayout;
    private RelativeLayout addTranslationButton;
    private RecyclerView languagesRecyclerView;
    private TranslationsListAdapter translationsListAdapter = new TranslationsListAdapter();

    private List<String> photoUris = new ArrayList<>();
    private List<String> mapsUris = new ArrayList<>();
    private ArrayList<Language> languagesList = new ArrayList<>();
    private ArrayList<SealifeShort> sealifes = new ArrayList<>();
    private Map<String, TextView> errorsMap = new HashMap<>();
    private FiltersResponseEntity filters;
    private boolean isShownMapsPhotos = false;

    private RequestBody requestIsWorkingHere, requestIsEdit = null, translations, requestCoverNumber, requestCoverId, requestLat, requestLng, requestDepth, requestCurrents, requestLevel, requestObject, requestMinVisibility, requestMaxVisibility, requsetCountryCode, requestId;
    private List<MultipartBody.Part> sealife = new ArrayList<>();
    private List<MultipartBody.Part> newImages = new ArrayList<>();
    private List<MultipartBody.Part> deletedImages = new ArrayList<>();
    private List<MultipartBody.Part> newMaps = new ArrayList<>();
    private List<MultipartBody.Part> deletedMaps = new ArrayList<>();
    private boolean isFromMap;
    private ArrayList<String> languages = new ArrayList<>();
    private Map<String, Translation> languagesMap = new HashMap<>();
    private ArrayList<SpotPhotoEditScreenEntity> userPhotosIds = new ArrayList<>();
    private ArrayList<String> userMapsIds = new ArrayList<>();
    private DiveSpotDetailsEntity diveSpotDetailsEntity;
    private boolean isPhotosRequestEnd = false;
    private boolean isTranslationsRequestEnd = false;
    private boolean isMapsRequestEnd = false;
    private ArrayList<Translation> translationsList = new ArrayList<>();

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>> mapsResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotPhoto> result) {
            isMapsRequestEnd = true;
            SpotPhotoEditScreenEntity photo;
            if (result != null) {
                for (DiveSpotPhoto diveSpotPhoto : result) {
                    if (diveSpotPhoto.getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                        userMapsIds.add(diveSpotPhoto.getId());
                    }
                }
            }
            setupUiAfterRequests();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

    };

    private DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity> photosResultListener = new DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity>() {
        @Override
        public void onSuccess(DiveSpotPhotosResponseEntity result) {
            isPhotosRequestEnd = true;
            SpotPhotoEditScreenEntity photo;
            String coverId = diveSpotDetailsEntity.getCoverPhotoId();
            if (result.getDiveSpotPhotos() != null) {
                for (DiveSpotPhoto diveSpotPhoto : result.getDiveSpotPhotos()) {
                    photo = new SpotPhotoEditScreenEntity();
                    photo.setAuthorId(diveSpotPhoto.getAuthor().getId());
                    photo.setCover(coverId.equals(diveSpotPhoto.getId()));
                    photo.setPhotoPath(diveSpotPhoto.getId());
                    if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId().equals(diveSpotPhoto.getAuthor().getId())) {
                        userPhotosIds.add(0, photo);
                    } else {
//                        userPhotosIds.add(photo);
                    }

                }
            }
            setupUiAfterRequests();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

    };

    private DDScannerRestClient.ResultListener<Void> updateDiveSpotResultListener = new DDScannerRestClient.ResultListener<Void>() {
        @Override
        public void onSuccess(Void result) {
            progressDialogUpload.dismiss();
            EventsTracker.trackDivespotEdited();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onConnectionFailure() {
            progressDialogUpload.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            progressDialogUpload.dismiss();
            switch (errorType) {
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case RIGHTS_NOT_FOUND_403:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_no_rights_to_edit_dive_spot);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
//                    Helpers.errorHandling(errorsMap, (ValidationError) errorData);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_unexpected_error);
                    break;
            }
        }

        @Override
        public void onInternetConnectionClosed() {
            progressDialogUpload.dismiss();
            UserActionInfoDialogFragment.show(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, false);
        }

    };

    private DDScannerRestClient.ResultListener<ArrayList<Translation>> translationsResultListener = new DDScannerRestClient.ResultListener<ArrayList<Translation>>() {
        @Override
        public void onSuccess(ArrayList<Translation> result) {
            translationsList = result;
            isTranslationsRequestEnd = true;
            setupUiAfterRequests();
        }

        @Override
        public void onConnectionFailure() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

        @Override
        public void onInternetConnectionClosed() {
            UserActionInfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_internet_connection_title, R.string.error_internet_connection, DialogsRequestCodes.DRC_EDIT_SPOT_ACTIVITY_HIDE, false);
        }

    };

    public static void showForResult(String diveSpotGson, Activity context, int requestCode) {
        Intent intent = new Intent(context, EditDiveSpotActivity.class);
        intent.putExtra("divespot", diveSpotGson);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventsTracker.trackDiveSpotEdit();
        setContentView(R.layout.activity_add_dive_spot);
        diveSpotDetailsEntity = new Gson().fromJson(getIntent().getStringExtra("divespot"), DiveSpotDetailsEntity.class);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotsTranslations(String.valueOf(diveSpotDetailsEntity.getId()), translationsResultListener);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotPhotos(String.valueOf(diveSpotDetailsEntity.getId()), photosResultListener);
        DDScannerApplication.getInstance().getDdScannerRestClient(this).getDiveSpotMaps(String.valueOf(diveSpotDetailsEntity.getId()), mapsResultListener);
        isFromMap = getIntent().getBooleanExtra(Constants.ADD_DIVE_SPOT_INTENT_IS_FROM_MAP, false);
        languages.add("Language");
        setupToolbar(R.string.edit_dive_spot, R.id.toolbar);
        findViewsAndSetupCurrentData(diveSpotDetailsEntity);
        makeErrorsMap();
        init();
    }

    private void setupUiAfterRequests() {
        if (isTranslationsRequestEnd && isPhotosRequestEnd && isMapsRequestEnd) {
            setUi();
        }
    }

    private void init() {
        diveSpotLocation = diveSpotDetailsEntity.getPosition();
        if (diveSpotDetailsEntity.getSealifes() != null) {
            sealifes = (ArrayList<SealifeShort>) diveSpotDetailsEntity.getSealifes();
        }
    }

    private void findViewsAndSetupCurrentData(DiveSpotDetailsEntity diveSpot) {
        isWorkingSwitch = findViewById(R.id.switch_button_working);
        isEditSwitch = findViewById(R.id.switch_button_edit);
        workingLayout = findViewById(R.id.working_layout);
        isEditLayout = findViewById(R.id.edit_layout);
        depth = findViewById(R.id.depth);
        depth.setText(diveSpot.getDepth());
        errorVisibility = findViewById(R.id.error_visibility);
        maxVisibilityHint = findViewById(R.id.max_visibility_hint);
        minVisibilityHint = findViewById(R.id.min_visibility_hint);
        errorCurrent = findViewById(R.id.error_current);
        errorTranslations = findViewById(R.id.error_translations);
        errorLevel = findViewById(R.id.error_level);
        errorObject = findViewById(R.id.error_object);
        errorCountry = findViewById(R.id.error_country);
        btnAddSealife = findViewById(R.id.btn_add_sealife);
        diveSpotPhotosRecyclrView = findViewById(R.id.photos_rc);
        levelAppCompatSpinner = findViewById(R.id.level_spinner);
        objectAppCompatSpinner = findViewById(R.id.object_spinner);
        currentsAppCompatSpinner = findViewById(R.id.currents_spinner);
        pickLocation = findViewById(R.id.location_layout);
        locationTitle = findViewById(R.id.location);
        btnSave = findViewById(R.id.button_create);
        sealifesRc = findViewById(R.id.sealifes_rc);
        mainLayout = findViewById(R.id.main_layout);
        progressView = findViewById(R.id.progressBarFull);
        errorDepth = findViewById(R.id.error_depth);
        errorLocation = findViewById(R.id.error_location);
        errorImages = findViewById(R.id.error_images);
        errorSealife = findViewById(R.id.error_sealife);
        errorVisibilityMax = findViewById(R.id.error_visibility_max);
        errorVisibilityMin = findViewById(R.id.error_visibility_min);
        visibilityMax = findViewById(R.id.maxVisibility);
        visibilityMax.setText(diveSpot.getVisibilityMax());
        visibilityMin = findViewById(R.id.minVisibility);
        visibilityMin.setText(diveSpot.getVisibilityMin());
        photos = findViewById(R.id.photos);
        maps = findViewById(R.id.maps);
        mapsRecyclerView = findViewById(R.id.maps_rc);
        LinearLayout countryLayout = findViewById(R.id.country_layout);
        countryTitle = findViewById(R.id.country_title);
        countryLayout.setOnClickListener(this);
        addTranslationButton = findViewById(R.id.button_add_language);
        languagesRecyclerView = findViewById(R.id.languages_list);
    }

    private void setUi() {
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(Helpers.convertDpToIntPixels(4, this), Helpers.convertDpToIntPixels(4, this));
        if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
            workingLayout.setVisibility(View.VISIBLE);
            if (diveSpotDetailsEntity.getFlags().isWorkingHere()) {
                isWorkingSwitch.setChecked(true);
                isEditLayout.setVisibility(View.VISIBLE);
                if (diveSpotDetailsEntity.getFlags().isEditable()) {
                    isEditSwitch.setChecked(true);
                }
            }
        }
        languagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        languagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        languagesRecyclerView.setAdapter(translationsListAdapter);
        translationsListAdapter.addTranslationsList(translationsList);
        addTranslationButton.setOnClickListener(this);
        isEditSwitch.setOnCheckedChangeListener(this);
        isWorkingSwitch.setOnCheckedChangeListener(this);
        requsetCountryCode = Helpers.createRequestBodyForString(diveSpotDetailsEntity.getCountry().getCode());
        countryTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        countryTitle.setText(diveSpotDetailsEntity.getCountry().getName());
        photosListAdapter.addServerPhoto(userPhotosIds);
        mapsListAdapter.addServerPhoto(userMapsIds);
        locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        progressDialogUpload = Helpers.getMaterialDialog(this);
        ProgressDialog progressDialog = new ProgressDialog(this);
        setAppCompatSpinnerValues(currentsAppCompatSpinner, Helpers.getListOfCurrentsTypes(), "Current", diveSpotDetailsEntity.getCurrents());
        setAppCompatSpinnerValues(levelAppCompatSpinner, Helpers.getDiveLevelTypes(), "Diver level", diveSpotDetailsEntity.getDiverLevel());
        setAppCompatSpinnerValues(objectAppCompatSpinner, Helpers.getDiveSpotTypes(), "Object", diveSpotDetailsEntity.getObject());
        btnSave.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);
        maps.setOnClickListener(this);
        /* Recycler view with images settings*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        diveSpotPhotosRecyclrView.setNestedScrollingEnabled(false);
        diveSpotPhotosRecyclrView.setHasFixedSize(false);
        diveSpotPhotosRecyclrView.setLayoutManager(layoutManager);
        diveSpotPhotosRecyclrView.setAdapter(photosListAdapter);
        LinearLayoutManager mapsLayoutManager = new LinearLayoutManager(EditDiveSpotActivity.this);
        mapsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mapsRecyclerView.setLayoutManager(mapsLayoutManager);
        mapsRecyclerView.setAdapter(mapsListAdapter);

        /* Recycler view with sealifes settings*/
        sealifesRc.addItemDecoration(itemDecoration);
        sealifesRc.setNestedScrollingEnabled(false);
        sealifesRc.setLayoutManager(ChipsLayoutManager.newBuilder(this).setOrientation(ChipsLayoutManager.HORIZONTAL).build());
        sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter(sealifes, this);
        sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);

        /*Toolbar settings*/

        progressDialog.setCancelable(false);
        progressView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_LOCATION:
                if (resultCode == RESULT_OK) {
                    this.diveSpotLocation = data.getParcelableExtra(ARG_LOCATION);
                    locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE:
                Helpers.hideKeyboard(this);
                if (resultCode == RESULT_OK) {
                    SealifeShort sealifeShort = (SealifeShort) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                    if (Helpers.checkIsSealifeAlsoInList(sealifes, sealifeShort.getId())) {
                        return;
                    }
                    sealifes.add(sealifeShort);
                    sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter(sealifes, this);
                    sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND:
                if (resultCode == RESULT_OK) {
                    sendUpdateDiveSpotRequest();
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
            case ActivitiesRequestCodes.REQUEST_CODE_PICK_LOCATION_ACTIVITY_PLACE_AUTOCOMPLETE:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    diveSpotLocation = place.getLatLng();
                    if (place.getAddress() != null) {
                        locationTitle.setText(place.getAddress());
                    }
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_COUNTRY:
                if (resultCode == RESULT_OK) {
                    countryTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
                    BaseIdNamePhotoEntity baseIdNamePhotoEntity = (BaseIdNamePhotoEntity)data.getSerializableExtra("country");
                    countryTitle.setText(baseIdNamePhotoEntity.getName());
                    requsetCountryCode = Helpers.createRequestBodyForString(baseIdNamePhotoEntity.getCode());
                }
                break;

        }
    }

    private void setAppCompatSpinnerValues(AppCompatSpinner spinner, List<String> values, String tag, String current) {
        ArrayList<String> objects = new ArrayList<>();
        objects.add(tag);
        objects.addAll(values);
        ArrayAdapter<String> adapter = new CharacteristicSpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
        spinner.setSelection(values.indexOf(current) + 1);
    }

    private void sendUpdateDiveSpotRequest() {
        progressDialogUpload.show();
        DDScannerApplication.getInstance().getDdScannerRestClient(this).postUpdateDiveSpot(updateDiveSpotResultListener, sealife, newImages, deletedImages, newMaps, deletedMaps, requestId, requestLat, requestLng, requsetCountryCode, requestDepth, requestLevel, requestCurrents, requestMinVisibility, requestMaxVisibility, requestCoverNumber,translations, requestObject, requestIsEdit, requestIsWorkingHere, requestCoverId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_layout:
                PickLocationActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_LOCATION, diveSpotLocation);
                break;
            case R.id.btn_add_sealife:
                SearchSealifeActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE, diveSpotLocation);
                break;
            case R.id.button_create:
                createRequestBodyies();
                break;
            case R.id.photos:
                changeViewState(photos, maps);
                diveSpotPhotosRecyclrView.setVisibility(View.VISIBLE);
                mapsRecyclerView.setVisibility(View.GONE);
                break;
            case R.id.maps:
                changeViewState(maps, photos);
                mapsRecyclerView.setVisibility(View.VISIBLE);
                diveSpotPhotosRecyclrView.setVisibility(View.GONE);
                break;
            case R.id.country_layout:
                PickCountryActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_COUNTRY);
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
        if (SharedPreferenceHelper.getActiveUserType() == SharedPreferenceHelper.UserType.DIVECENTER) {
            if (isWorkingSwitch.isChecked()) {
                requestIsWorkingHere = Helpers.createRequestBodyForString("1");
                if (isEditSwitch.isChecked()) {
                    requestIsEdit = Helpers.createRequestBodyForString("1");
                } else {
                    requestIsEdit = Helpers.createRequestBodyForString("0");
                }
            } else {
                requestIsWorkingHere = Helpers.createRequestBodyForString("0");
            }
        }
        if (photosListAdapter.getServerPhotoCoverId() != null) {
            requestCoverId = Helpers.createRequestBodyForString(String.valueOf(photosListAdapter.getServerPhotoCoverId()));
        }
        if (photosListAdapter.getDevicePhotoCoverNumber() != null) {
            requestCoverNumber = Helpers.createRequestBodyForString(String.valueOf(photosListAdapter.getDevicePhotoCoverNumber()));
        }
        translations = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), new Gson().toJson(translationsListAdapter.getTranslations()));
        requestObject = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getDiveSpotTypes().indexOf(objectAppCompatSpinner.getSelectedItem().toString()) + 1));
        requestCurrents = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getListOfCurrentsTypes().indexOf(currentsAppCompatSpinner.getSelectedItem().toString()) + 1));
        requestLevel = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getDiveLevelTypes().indexOf(levelAppCompatSpinner.getSelectedItem().toString())));
        requestMinVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMin.getText().toString());
        requestMaxVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMax.getText().toString());
        requestId = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(diveSpotDetailsEntity.getId()));
        sealife = new ArrayList<>();

        //check if sealifes not null
        if (sealifeListAddingDiveSpotAdapter != null && sealifeListAddingDiveSpotAdapter.getSealifes() != null) {
            sealifes = sealifeListAddingDiveSpotAdapter.getSealifes();
        } else {
            sealife = null;
        }

        //creating list of parts for sealifes
        if (sealife != null && sealifes.size() > 0) {
            for (int i = 0; i < sealifes.size(); i++) {
                sealife.add(MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE_ARRAY, sealifes.get(i).getId()));
            }
        }

        //create deleted photos list of part's
        if (photosListAdapter.getDeletedPhotos().size() > 0) {
            for (int i = 0; i < photosListAdapter.getDeletedPhotos().size(); i++) {
                deletedImages.add(MultipartBody.Part.createFormData("deleted_photos[]", photosListAdapter.getDeletedPhotos().get(i).getPhotoPath()));
            }
        } else {
            deletedImages = null;
        }

        //create deleted maps list of part's
        if (mapsListAdapter.getDeletedPhotos().size() > 0) {
            for (int i = 0; i < mapsListAdapter.getDeletedPhotos().size(); i++) {
                deletedMaps.add(MultipartBody.Part.createFormData("deleted_maps[]", mapsListAdapter.getDeletedPhotos().get(i)));
            }
        } else {
            deletedMaps = null;
        }

        //create new photos list of part's
        if (photosListAdapter.getNewPhotos().size() > 0) {
            deletedImages = new ArrayList<>();
            for (int i = 0; i < photosListAdapter.getNewPhotos().size(); i++) {
                File image = new File(photosListAdapter.getNewPhotos().get(i).getPhotoPath());
                image = Helpers.compressFile(image, this);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("new_photos[]", image.getName(), requestFile);
                newImages.add(part);
            }
        } else {
            newImages = null;
        }

        //create new maps list of part's
        if (mapsListAdapter.getNewPhotos().size() > 0) {
            newMaps = new ArrayList<>();
            for (int i = 0; i < mapsListAdapter.getNewPhotos().size(); i++) {
                File image = new File(mapsListAdapter.getNewPhotos().get(i));
                image = Helpers.compressFile(image, this);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("new_maps[]", image.getName(), requestFile);
                newMaps.add(part);
            }
        } else {
            newMaps = null;
        }
        sendUpdateDiveSpotRequest();
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
//            minVisibilityHint.setVisibility(View.GONE);
//            maxVisibilityHint.setVisibility(View.GONE);
            downestCoordinate = visibilityMax.getBottom();
        } else {
            downestCoordinate = visibilityMax.getBottom();
            if (Float.parseFloat(visibilityMax.getText().toString()) <= Float.parseFloat(visibilityMin.getText().toString())) {
                isSomethingWrong = true;
                errorVisibility.setVisibility(View.VISIBLE);
                errorVisibility.setText(R.string.error_visivibility_append);
//                minVisibilityHint.setVisibility(View.GONE);
//                maxVisibilityHint.setVisibility(View.GONE);
            }
            if (visibilityMin.getText().toString().isEmpty() || Float.parseFloat(visibilityMin.getText().toString()) < 1 || Float.parseFloat(visibilityMin.getText().toString()) > 100) {
                isSomethingWrong = true;
                errorVisibility.setVisibility(View.GONE);
                errorVisibilityMin.setVisibility(View.VISIBLE);
//                minVisibilityHint.setVisibility(View.GONE);
            }

            if (visibilityMax.getText().toString().isEmpty() || Float.parseFloat(visibilityMax.getText().toString()) < 1 || Float.parseFloat(visibilityMax.getText().toString()) > 100) {
                isSomethingWrong = true;
                errorVisibility.setVisibility(View.GONE);
                errorVisibilityMax.setVisibility(View.VISIBLE);
//                maxVisibilityHint.setVisibility(View.GONE);
            }

        }

        if (depth.getText().toString().isEmpty()) {
            isSomethingWrong = true;
            errorDepth.setVisibility(View.VISIBLE);
            downestCoordinate = depth.getBottom();
        } else {
            downestCoordinate = depth.getBottom();
            if (Float.parseFloat(depth.getText().toString()) > 1092) {
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

        if (translationsListAdapter.getTranslations().size() < 1) {
            isSomethingWrong = true;
            errorTranslations.setVisibility(View.VISIBLE);
            downestCoordinate = addTranslationButton.getBottom();
        }

//        if (photosListAdapter.getItemCount() < 2) {
//            isSomethingWrong = true;
//            errorImages.setVisibility(View.VISIBLE);
//            downestCoordinate = errorImages.getBottom();
//        }

        if (isSomethingWrong) {
            scrollToError(downestCoordinate);
        }

        return isSomethingWrong;

    }

    private void scrollToError(final int bottom) {
        new Handler().post(() -> mainLayout.scrollTo(0, bottom));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DialogHelpers.showDialogAfterChangesInActivity(getSupportFragmentManager());
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
    }

    @Subscribe
    public void deleteImage(ImageDeletedEvent event) {
        if (diveSpotPhotosRecyclrView.getVisibility() == View.VISIBLE) {
            photoUris.remove(event.getImageIndex());
            diveSpotPhotosRecyclrView.setAdapter(new AddPhotoToDsListAdapter(photoUris, EditDiveSpotActivity.this));
            return;
        }
        mapsUris.remove(event.getImageIndex());
        mapsRecyclerView.setAdapter(new AddPhotoToDsListAdapter(mapsUris, EditDiveSpotActivity.this));
    }

    @Override
    public void onDialogClosed(int requestCode) {
        finish();
    }

    @Subscribe
    public void pickPhotoFrom(AddPhotoDoListEvent event) {
        pickPhotosFromGallery();
    }

    @Subscribe
    public void addLanguageToList(AddTranslationClickedEvent event) {
        PickLanguageActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LANGUAGE);
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
    public void onPictureFromCameraTaken(File picture) {

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
        if (diveSpotPhotosRecyclrView.getVisibility() == View.VISIBLE) {
            photosListAdapter.addDevicePhotos(photos);
        } else {
            mapsListAdapter.addDevicePhotos(pictures);
        }
    }

    @Override
    public void onNegativeDialogClicked() {

    }

    @Override
    public void onPositiveDialogClicked() {
        finish();
    }

}
