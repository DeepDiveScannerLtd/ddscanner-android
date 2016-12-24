package com.ddscanner.screens.divespot.edit;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Language;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.Translation;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.events.AddTranslationClickedEvent;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.PickLanguageActivity;
import com.ddscanner.ui.activities.SearchSealifeActivity;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.CharacteristicSpinnerItemsAdapter;
import com.ddscanner.ui.adapters.LanguagesSpinnerAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.rey.material.widget.ProgressView;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditDiveSpotActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = EditDiveSpotActivity.class.getSimpleName();

    private static final String DIVE_SPOT_NAME_PATTERN = "^[a-zA-Z0-9 ]*$";
    private EditSpotPhotosListAdapter photosListAdapter = new EditSpotPhotosListAdapter(EditDiveSpotActivity.this);
    private EditSpotPhotosListAdapter mapsListAdapter = new EditSpotPhotosListAdapter(EditDiveSpotActivity.this);
    private ImageButton btnAddPhoto;
    private LinearLayout btnAddSealife;

    private Toolbar toolbar;
    private LatLng diveSpotLocation;
    private LatLngBounds diveSpotLatLngBounds;

    private LinearLayout pickLocation;
    private RecyclerView diveSpotPhotosRecyclrView;
    private TextView addPhotoTitle;
    private TextView locationTitle;
    private AppCompatSpinner levelAppCompatSpinner;
    private AppCompatSpinner currentsAppCompatSpinner;
    private AppCompatSpinner objectAppCompatSpinner;
    private EditText name;
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
    private TextView error_name;
    private TextView error_location;
    private TextView error_description;
    private TextView error_depth;
    private TextView error_sealife;
    private TextView error_images;
    private TextView error_visibility_min;
    private TextView error_visibility_max;
    private int maxPhotos = 3;
    private TextView photos;
    private TextView maps;
    private RecyclerView mapsRecyclerView;
    private AppCompatSpinner languageAppCompatSpinner;

    private List<String> photoUris = new ArrayList<>();
    private List<String> mapsUris = new ArrayList<>();
    private ArrayList<Language> languagesList = new ArrayList<>();
    private ArrayList<SealifeShort> sealifes = new ArrayList<>();
    private Map<String, TextView> errorsMap = new HashMap<>();
    private FiltersResponseEntity filters;
    private boolean isShownMapsPhotos = false;
    private int previousPosition = -1;

    private RequestBody translations, requestCoverNumber, requestLat, requestLng, requestDepth, requestCurrents, requestLevel, requestObject, requestMinVisibility, requestMaxVisibility, requsetCountryCode, requestId;
    private List<MultipartBody.Part> sealife = new ArrayList<>();
    private List<MultipartBody.Part> newImages = new ArrayList<>();
    private List<MultipartBody.Part> deletedImages = new ArrayList<>();
    private List<MultipartBody.Part> newMaps = new ArrayList<>();
    private List<MultipartBody.Part> deletedMaps = new ArrayList<>();
    private boolean isFromMap;
    private ArrayList<String> languages = new ArrayList<>();
    private Map<String, Translation> languagesMap = new HashMap<>();
    private ArrayList<String> userPhotosIds = new ArrayList<>();
    private ArrayList<String> userMapsIds = new ArrayList<>();
    private DiveSpotDetailsEntity diveSpotDetailsEntity;
    private boolean isPhotosRequestEnd = false;
    private boolean isTranslationsRequestEnd = false;
    private boolean isMapsRequestEnd = false;

    private DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>> mapsResultListener = new DDScannerRestClient.ResultListener<ArrayList<DiveSpotPhoto>>() {
        @Override
        public void onSuccess(ArrayList<DiveSpotPhoto> result) {
            isMapsRequestEnd = true;
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

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }
    };

    private DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity> photosResultListener = new DDScannerRestClient.ResultListener<DiveSpotPhotosResponseEntity>() {
        @Override
        public void onSuccess(DiveSpotPhotosResponseEntity result) {
            isPhotosRequestEnd = true;
            if (result.getDiveSpotPhotos() != null) {
                for (DiveSpotPhoto diveSpotPhoto : result.getDiveSpotPhotos()) {
                    if (diveSpotPhoto.getAuthor().getId().equals(DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserServerId())) {
                        userPhotosIds.add(diveSpotPhoto.getId());
                    }
                }
            }
            setupUiAfterRequests();
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

        }
    };

    private DDScannerRestClient.ResultListener<String> countryResultListener = new DDScannerRestClient.ResultListener<String>() {
        @Override
        public void onSuccess(String result) {
            requsetCountryCode = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), result);
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

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
            InfoDialogFragment.show(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            progressDialogUpload.dismiss();
            switch (errorType) {
                case UNAUTHORIZED_401:
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().logout();
                    LoginActivity.showForResult(EditDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    break;
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_message_dive_spot_not_found);
                    break;
                case RIGHTS_NOT_FOUND_403:
                    // This is unexpected so track it
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_no_rights_to_edit_dive_spot);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
                    Helpers.errorHandling(errorsMap, (ValidationError) errorData);
                    break;
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage, R.string.error_server_error_title, R.string.error_unexpected_error);
                    break;
            }
        }
    };

    private DDScannerRestClient.ResultListener<ArrayList<Translation>> translationsResultListener = new DDScannerRestClient.ResultListener<ArrayList<Translation>>() {
        @Override
        public void onSuccess(ArrayList<Translation> result) {
            for (Translation translation : result) {
                languages.add(translation.getLanguage());
                languagesMap.put(translation.getCode(), translation);
                languagesList.add(translation.getLanguageEntity());
            }
            isTranslationsRequestEnd = true;
            setupUiAfterRequests();
        }

        @Override
        public void onConnectionFailure() {

        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {

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
        setContentView(R.layout.activity_add_dive_spot);
        EventsTracker.trackDiveSpotCreation();
        diveSpotDetailsEntity = new Gson().fromJson(getIntent().getStringExtra("divespot"), DiveSpotDetailsEntity.class);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotsTranslations(String.valueOf(diveSpotDetailsEntity.getId()), translationsResultListener);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotPhotos(String.valueOf(diveSpotDetailsEntity.getId()), photosResultListener);
        DDScannerApplication.getInstance().getDdScannerRestClient().getDiveSpotMaps(String.valueOf(diveSpotDetailsEntity.getId()), mapsResultListener);
        isFromMap = getIntent().getBooleanExtra(Constants.ADD_DIVE_SPOT_INTENT_IS_FROM_MAP, false);
        languages.add("Language");
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
        diveSpotLatLngBounds = new LatLngBounds(new LatLng(diveSpotLocation.latitude - 1, diveSpotLocation.longitude - 1), new LatLng(diveSpotLocation.latitude + 1, diveSpotLocation.longitude + 1));
        sealifes =(ArrayList<SealifeShort>) diveSpotDetailsEntity.getSealifes();
    }

    private void findViewsAndSetupCurrentData(DiveSpotDetailsEntity diveSpot) {
        name = (EditText) findViewById(R.id.name);
        depth = (EditText) findViewById(R.id.depth);
        depth.setText(diveSpot.getDepth());
        description = (EditText) findViewById(R.id.description);

        btnAddSealife = (LinearLayout) findViewById(R.id.btn_add_sealife);
        diveSpotPhotosRecyclrView = (RecyclerView) findViewById(R.id.photos_rc);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        levelAppCompatSpinner = (AppCompatSpinner) findViewById(R.id.level_spinner);
        objectAppCompatSpinner = (AppCompatSpinner) findViewById(R.id.object_spinner);
        currentsAppCompatSpinner = (AppCompatSpinner) findViewById(R.id.currents_spinner);
        languageAppCompatSpinner = (AppCompatSpinner) findViewById(R.id.language_spinner);
        pickLocation = (LinearLayout) findViewById(R.id.location_layout);
        locationTitle = (TextView) findViewById(R.id.location);
        btnSave = (Button) findViewById(R.id.button_create);
        sealifesRc = (RecyclerView) findViewById(R.id.sealifes_rc);
        mainLayout = (ScrollView) findViewById(R.id.main_layout);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        error_depth = (TextView) findViewById(R.id.error_depth);
        error_description = (TextView) findViewById(R.id.error_description);
        error_location = (TextView) findViewById(R.id.error_location);
        error_name = (TextView) findViewById(R.id.error_name);
        error_images = (TextView) findViewById(R.id.error_images);
        error_sealife = (TextView) findViewById(R.id.error_sealife);
        error_visibility_max = (TextView) findViewById(R.id.error_visibility_max);
        error_visibility_min = (TextView) findViewById(R.id.error_visibility_min);
        visibilityMax = (EditText) findViewById(R.id.maxVisibility);
        visibilityMax.setText(diveSpot.getVisibilityMax());
        visibilityMin = (EditText) findViewById(R.id.minVisibility);
        visibilityMin.setText(diveSpot.getVisibilityMin());
        photos = (TextView) findViewById(R.id.photos);
        maps = (TextView) findViewById(R.id.maps);
        mapsRecyclerView = (RecyclerView) findViewById(R.id.maps_rc);
    }

    private void setUi() {
        diveSpotDetailsEntity.setPhotos(userPhotosIds);
        diveSpotDetailsEntity.setMaps(userMapsIds);
        if (diveSpotDetailsEntity.getPhotos() != null) {
            photosListAdapter.addServerPhoto((ArrayList<String>) diveSpotDetailsEntity.getPhotos());
        }
        if (diveSpotDetailsEntity.getMaps() != null) {
            mapsListAdapter.addServerPhoto((ArrayList<String>) diveSpotDetailsEntity.getMaps());
        }
        locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        languageAppCompatSpinner.setAdapter(new LanguagesSpinnerAdapter(this, R.layout.item_language_spinner, languages));
        languageAppCompatSpinner.setSelection(1);
        languageAppCompatSpinner.setOnItemSelectedListener(this);
        progressDialogUpload = Helpers.getMaterialDialog(this);
        ProgressDialog progressDialog = new ProgressDialog(this);
        name.setEnabled(false);
        description.setEnabled(false);
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
        LinearLayoutManager sealifeLayoutManager = new LinearLayoutManager(
                EditDiveSpotActivity.this);
        sealifeLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sealifesRc.setNestedScrollingEnabled(false);
        sealifesRc.setHasFixedSize(false);
        sealifesRc.setLayoutManager(sealifeLayoutManager);
        sealifesRc.setAdapter(new SealifeListAddingDiveSpotAdapter(sealifes, this));

        /*Toolbar settings*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_dive_spot);

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
                    this.diveSpotLocation = data.getParcelableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_LATLNG);
                    DDScannerApplication.getInstance().getDdScannerRestClient().getCountryCode(String.valueOf(diveSpotLocation.latitude), String.valueOf(diveSpotLocation.longitude), countryResultListener);
                    if (data.getStringExtra(Constants.ADD_DIVE_SPOT_INTENT_LOCATION_NAME) != null) {
                        locationTitle.setText(data.getStringExtra(Constants.ADD_DIVE_SPOT_INTENT_LOCATION_NAME));
                    } else {
                        locationTitle.setText(R.string.location);
                    }
                    locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (diveSpotPhotosRecyclrView.getVisibility() == View.VISIBLE) {
                        photosListAdapter.addDevicePhotos(Helpers.getPhotosFromIntent(data, this));
                        diveSpotPhotosRecyclrView.scrollToPosition(photosListAdapter.getItemCount());
                        break;
                    }
                    mapsListAdapter.addDevicePhotos(Helpers.getPhotosFromIntent(data, this));
                    mapsRecyclerView.scrollToPosition(photosListAdapter.getItemCount());
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE:
                Helpers.hideKeyboard(this);
                if (resultCode == RESULT_OK) {
                    SealifeShort sealifeShort = (SealifeShort) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);

                    if (Helpers.checkIsSealifeAlsoInList((ArrayList<SealifeShort>) sealifes, sealifeShort.getId())) {
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.sealife_already_added);
                        return;
                    }
                    sealifes.add(sealifeShort);
                    sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter((ArrayList<SealifeShort>) sealifes, this);
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
                    addLanguageToList(language);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_PICK_LOCATION_ACTIVITY_PLACE_AUTOCOMPLETE:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    diveSpotLocation = place.getLatLng();
                }
                break;
        }
    }

    private void setAppCompatSpinnerValues(AppCompatSpinner spinner, List<String> values, String tag, String current) {
        ArrayList<String> objects = new ArrayList<String>();
        objects.add(tag);
        objects.addAll(values);
        ArrayAdapter<String> adapter = new CharacteristicSpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
        spinner.setSelection(values.indexOf(current) + 1);
    }

    private void sendUpdateDiveSpotRequest() {
        progressDialogUpload.show();
        //   DDScannerApplication.getInstance().getDdScannerRestClient().postAddDiveSpot(addDiveSpotResultListener, sealife, images, requestName, requestLat, requestLng, requestDepth, requestMinVisibility, requestMaxVisibility, requestCurrents, requestLevel, requestObject, requestDescription, requestToken, requestSocial, requestSecret);
//        DDScannerApplication.getInstance().getDdScannerRestClient().postAddDiveSpot(resultListener, sealife, images, mapsList, requestLat, requestLng, requsetCountryCode, requestDepth, requestLevel, requestCurrents, requestMinVisibility, requestMaxVisibility, requestCoverNumber, translations, requestObject);
        DDScannerApplication.getInstance().getDdScannerRestClient().postUpdateDiveSpot(updateDiveSpotResultListener, sealife, newImages, deletedImages, newMaps, deletedMaps, requestId, requestLat, requestLng, requsetCountryCode, requestDepth, requestLevel, requestCurrents, requestMinVisibility, requestMaxVisibility, requestCoverNumber,translations, requestObject);
    }

    private void pickPhotoFromGallery() {
        if (checkReadStoragePermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_PHOTO);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PERMISSION_READ_STORAGE);
        }
    }

    public boolean checkReadStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void showPlacePikerIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            builder.setLatLngBounds(diveSpotLatLngBounds);
            startActivityForResult(builder.build(this), ActivitiesRequestCodes.REQUEST_CODE_PICK_LOCATION_ACTIVITY_PLACE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(TAG, e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(TAG, e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                pickPhotoFromGallery();
                break;
            case R.id.location_layout:
                showPlacePikerIntent();
                break;
            case R.id.btn_add_sealife:
                Intent sealifeIntent = new Intent(EditDiveSpotActivity.this,
                        SearchSealifeActivity.class);
                startActivityForResult(sealifeIntent, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE);
                break;
            case R.id.button_create:
//                if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
                createRequestBodyies();
//                } else {
//                    Intent loginIntent = new Intent(this, SocialNetworks.class);
//                    startActivityForResult(loginIntent, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN);
//                }
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
        if (!name.getText().toString().matches(DIVE_SPOT_NAME_PATTERN)) {
            error_name.setVisibility(View.VISIBLE);
            error_name.setText(R.string.errr);
            return;
        }
        error_name.setVisibility(View.GONE);
        requestDepth = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                depth.getText().toString().trim());
        if (diveSpotLocation != null) {
            requestLat = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    String.valueOf(diveSpotLocation.latitude));
            requestLng = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    String.valueOf(diveSpotLocation.longitude));
        }
        translations = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), new Gson().toJson(languagesMap.values()));
        requestCoverNumber = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(1));
        requestObject = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getDiveSpotTypes().indexOf(objectAppCompatSpinner.getSelectedItem().toString()) + 1));
        requestCurrents = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getListOfCurrentsTypes().indexOf(currentsAppCompatSpinner.getSelectedItem().toString()) + 1));
        requestLevel = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), String.valueOf(Helpers.getDiveLevelTypes().indexOf(levelAppCompatSpinner.getSelectedItem().toString()) + 1));
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
                deletedImages.add(MultipartBody.Part.createFormData("deleted_photos[]", photosListAdapter.getDeletedPhotos().get(i)));
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
                File image = new File(photosListAdapter.getNewPhotos().get(i));
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
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("new_maps[]", image.getName(), requestFile);
                newMaps.add(part);
            }
        } else {
            newMaps = null;
        }
        sendUpdateDiveSpotRequest();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogHelpers.showDialogAfterChanging(R.string.dialog_leave_title, R.string.dialog_leave_spot_message, this, this);
                // onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideErrorsFields() {
        for (Map.Entry<String, TextView> entry : errorsMap.entrySet()) {
            entry.getValue().setVisibility(View.GONE);
        }
    }

    private void makeErrorsMap() {
        errorsMap.put("depth", error_depth);
        errorsMap.put("name", error_name);
        errorsMap.put("description", error_description);
        errorsMap.put("location", error_location);
        errorsMap.put("images", error_images);
        errorsMap.put("sealife", error_sealife);
        errorsMap.put("visibilityMin", error_visibility_min);
        errorsMap.put("visibilityMax", error_visibility_max);
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
        DDScannerApplication.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        DDScannerApplication.bus.unregister(this);
    }

    @Subscribe
    public void deleteImage(ImageDeletedEvent event) {
        maxPhotos++;
        if (diveSpotPhotosRecyclrView.getVisibility() == View.VISIBLE) {
            photoUris.remove(event.getImageIndex());
            diveSpotPhotosRecyclrView.setAdapter(new AddPhotoToDsListAdapter(photoUris, EditDiveSpotActivity.this));
            return;
        }
        mapsUris.remove(event.getImageIndex());
        mapsRecyclerView.setAdapter(new AddPhotoToDsListAdapter(mapsUris, EditDiveSpotActivity.this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhotoFromGallery();
                } else {
                    Toast.makeText(EditDiveSpotActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_CONNECTION_ERROR:
            case DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR:
                finish();
        }
    }

    @Subscribe
    public void pickPhotoFrom(AddPhotoDoListEvent event) {
        pickPhotoFromGallery();
    }

    @Subscribe
    public void addLanguageToList(AddTranslationClickedEvent event) {
        PickLanguageActivity.showForResult(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LANGUAGE, true);
    }

    private void addLanguageToList(Language language) {
        languagesList.add(language);
        languages.add(language.getName());
        languagesMap.put(language.getCode(), new Translation());
        languageAppCompatSpinner.setAdapter(new LanguagesSpinnerAdapter(this, R.layout.item_language_spinner, languages));
        languageAppCompatSpinner.setSelection(languages.size() - 1);
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(languageAppCompatSpinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
        name.setEnabled(true);
        description.setEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (i != 0) {
            if (previousPosition != -1) {
                Translation translation =  languagesMap.get(languagesList.get(previousPosition).getCode());
                translation.setName(name.getText().toString());
                translation.setDescription(description.getText().toString());
                translation.setLanguage(languagesList.get(previousPosition).getCode());
                languagesMap.put(languagesList.get(previousPosition).getCode(), translation);
            }
            description.setText("");
            name.setText("");
            if (languagesMap.get(languagesList.get(i-1).getCode()).getName() != null) {
                name.setText(languagesMap.get(languagesList.get(i-1).getCode()).getName());
            }
            if (languagesMap.get(languagesList.get(i-1).getCode()).getDescription() != null) {
                description.setText(languagesMap.get(languagesList.get(i-1).getCode()).getDescription());
            }
            previousPosition = i-1;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
