package com.ddscanner.screens.divespot.add;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.events.AddPhotoDoListEvent;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.screens.divespot.details.DiveSpotDetailsActivity;
import com.ddscanner.ui.activities.LoginActivity;
import com.ddscanner.ui.activities.PickLocationActivity;
import com.ddscanner.ui.activities.SearchSealifeActivity;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.CharacteristicSpinnerItemsAdapter;
import com.ddscanner.ui.adapters.LanguagesSpinnerAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.ui.dialogs.InfoDialogFragment.DialogClosedListener;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;
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

public class AddDiveSpotActivity extends AppCompatActivity implements View.OnClickListener, DialogClosedListener {

    private static final String TAG = AddDiveSpotActivity.class.getSimpleName();
    private static final String DIVE_SPOT_NAME_PATTERN = "^[a-zA-Z0-9 ]*$";

    private ImageButton btnAddPhoto;
    private LinearLayout btnAddSealife;

    private Toolbar toolbar;
    private LatLng diveSpotLocation;

    private LinearLayout pickLocation;
    private RecyclerView photos_rc;
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
    private List<SealifeShort> sealifes = new ArrayList<>();
    private Map<String, TextView> errorsMap = new HashMap<>();
    private FiltersResponseEntity filters;
    private boolean isShownMapsPhotos = false;

    private RequestBody requestName, requestLat, requestLng,
            requestDepth, requestCurrents,
            requestLevel, requestObject,
            requestDescription, requestSocial, requestToken,
            requestSecret, requestMinVisibility, requestMaxVisibility;
    private List<MultipartBody.Part> sealife = new ArrayList<>();
    private List<MultipartBody.Part> images = new ArrayList<>();
    private boolean isFromMap;

    private DDScannerRestClient.ResultListener<FiltersResponseEntity> filtersResultListener = new DDScannerRestClient.ResultListener<FiltersResponseEntity>() {
        @Override
        public void onSuccess(FiltersResponseEntity result) {
            filters = result;
            setAppCompatSpinnerValues(objectAppCompatSpinner, filters.getObject(), "Object");
            setAppCompatSpinnerValues(levelAppCompatSpinner, filters.getLevel(), "Level");
            setAppCompatSpinnerValues(currentsAppCompatSpinner, filters.getCurrents(), "Current");
            progressView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_CONNECTION_ERROR, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_unexpected_error, R.string.error_connection_failed, DialogsRequestCodes.DRC_ADD_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR, false);
        }
    };

    private DDScannerRestClient.ResultListener<DiveSpotShort> addDiveSpotResultListener = new DDScannerRestClient.ResultListener<DiveSpotShort>() {
        @Override
        public void onSuccess(DiveSpotShort diveSpotShort) {
            progressDialogUpload.dismiss();
            EventsTracker.trackDivespotCreated();
            showSuccessDialog(String.valueOf(diveSpotShort.getId()));
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
                    LoginActivity.showForResult(AddDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    break;
                case UNPROCESSABLE_ENTITY_ERROR_422:
                    Helpers.errorHandling(errorsMap, (ValidationError) errorData);
                    break;
                case SERVER_INTERNAL_ERROR_500:
                default:
                    Helpers.handleUnexpectedServerError(getSupportFragmentManager(), url, errorMessage);
            }
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
        findViews();
        setUi();
      //  DDScannerApplication.getInstance().getDdScannerRestClient().getFilters(filtersResultListener);
        makeErrorsMap();
    }

    private void findViews() {
        name = (EditText) findViewById(R.id.name);
        depth = (EditText) findViewById(R.id.depth);
        description = (EditText) findViewById(R.id.description);
        btnAddSealife = (LinearLayout) findViewById(R.id.btn_add_sealife);
        photos_rc = (RecyclerView) findViewById(R.id.photos_rc);
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
        visibilityMin = (EditText) findViewById(R.id.minVisibility);
        photos = (TextView) findViewById(R.id.photos);
        maps = (TextView) findViewById(R.id.maps);
        mapsRecyclerView = (RecyclerView) findViewById(R.id.maps_rc);
    }

    private void setUi() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Language");
        data.add("Thai");
        data.add("English");
        data.add("Russian");
        languageAppCompatSpinner.setAdapter(new LanguagesSpinnerAdapter(this, R.layout.item_language_spinner, data));
        progressDialogUpload = Helpers.getMaterialDialog(this);
        ProgressDialog progressDialog = new ProgressDialog(this);
        btnSave.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);
        maps.setOnClickListener(this);
        /* Recycler view with images settings*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
        photos_rc.setAdapter(new AddPhotoToDsListAdapter(photoUris, this));
        LinearLayoutManager mapsLayoutManager = new LinearLayoutManager(AddDiveSpotActivity.this);
        mapsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mapsRecyclerView.setLayoutManager(mapsLayoutManager);
        mapsRecyclerView.setAdapter(new AddPhotoToDsListAdapter(mapsUris, this));

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
                    this.diveSpotLocation = data.getParcelableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_LATLNG);
                    if (data.getStringExtra(Constants.ADD_DIVE_SPOT_INTENT_LOCATION_NAME) != null) {
                        locationTitle.setText(data.getStringExtra(Constants.ADD_DIVE_SPOT_INTENT_LOCATION_NAME));
                    } else {
                        locationTitle.setText(R.string.location);
                    }
                    locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_PHOTO:
                Uri uri = Uri.parse("");
                if (resultCode == RESULT_OK) {
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            String filename = "DDScanner" + String.valueOf(System.currentTimeMillis());
                            try {
                                uri = data.getClipData().getItemAt(i).getUri();
                                String mimeType = getContentResolver().getType(uri);
                                String sourcePath = getExternalFilesDir(null).toString();
                                File file = new File(sourcePath + "/" + filename);
                                if (Helpers.isFileImage(uri.getPath()) || mimeType.contains("image")) {
                                    try {
                                        Helpers.copyFileStream(file, uri, this);
                                        Log.i(TAG, file.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (photos_rc.getVisibility() == View.VISIBLE) {
                                        photoUris.add(file.getPath());
                                    } else {
                                        mapsUris.add(file.getPath());
                                    }
                                } else {
                                    Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (data.getData() != null) {
                        String filename = "DDScanner" + String.valueOf(System.currentTimeMillis());
                        try {
                            uri = data.getData();
                            String mimeType = getContentResolver().getType(uri);
                            String sourcePath = getExternalFilesDir(null).toString();
                            File file = new File(sourcePath + "/" + filename);
                            if (Helpers.isFileImage(uri.getPath()) || mimeType.contains("image")) {
                                try {
                                    Helpers.copyFileStream(file, uri, this);
                                    Log.i(TAG, file.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (photos_rc.getVisibility() == View.VISIBLE) {
                                    photoUris.add(file.getPath());
                                } else {
                                    mapsUris.add(file.getPath());
                                }
                            } else {
                                Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (photos_rc.getVisibility() == View.VISIBLE) {
                        photos_rc.setAdapter(new AddPhotoToDsListAdapter(photoUris, AddDiveSpotActivity.this));
                        photos_rc.scrollToPosition(photoUris.size());
                        break;
                    }
                    mapsRecyclerView.setAdapter(new AddPhotoToDsListAdapter(mapsUris, AddDiveSpotActivity.this));
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
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getInstance().getDdScannerRestClient().getFilters(filtersResultListener);
                } else {
                    finish();
                }
                break;
        }
    }

    private void setAppCompatSpinnerValues(AppCompatSpinner spinner, Map<String, String> values, String tag) {
        ArrayList<String> objects = new ArrayList<String>();
        objects.add(tag);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
        }
        ArrayAdapter<String> adapter = new CharacteristicSpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
    }

    private void makeAddDiveSpotRequest() {
        progressDialogUpload.show();
        DDScannerApplication.getInstance().getDdScannerRestClient().postAddDiveSpot(addDiveSpotResultListener, sealife, images, requestName, requestLat, requestLng, requestDepth, requestMinVisibility, requestMaxVisibility, requestCurrents, requestLevel, requestObject, requestDescription, requestToken, requestSocial, requestSecret);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                pickPhotoFromGallery();
                break;
            case R.id.location_layout:
                Intent intent = new Intent(AddDiveSpotActivity.this,
                        PickLocationActivity.class);
                startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_LOCATION);
                break;
            case R.id.btn_add_sealife:
                Intent sealifeIntent = new Intent(AddDiveSpotActivity.this,
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
                photos_rc.setVisibility(View.VISIBLE);
                mapsRecyclerView.setVisibility(View.GONE);
                break;
            case R.id.maps:
                changeViewState(maps, photos);
                mapsRecyclerView.setVisibility(View.VISIBLE);
                photos_rc.setVisibility(View.GONE);
                break;
        }
    }

    private void createSocialDatarequests() {
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            requestToken = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                        DDScannerApplication.getInstance().getSharedPreferenceHelper().getSecret());
            }
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
//        if (description.getText().toString().length() < 150) {
//            error_description.setVisibility(View.VISIBLE);
//            error_description.setText(R.string.description_length_error);
//            return;
//        }
        error_name.setVisibility(View.GONE);
        createSocialDatarequests();
        requestName = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                name.getText().toString().trim());
        requestDepth = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                depth.getText().toString().trim());
        if (diveSpotLocation != null) {
            requestLat = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    String.valueOf(diveSpotLocation.latitude));
            requestLng = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    String.valueOf(diveSpotLocation.longitude));
        }
        requestObject = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getObject())
                        .get(objectAppCompatSpinner.getSelectedItem().toString()));
        requestCurrents = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getCurrents())
                        .get(currentsAppCompatSpinner.getSelectedItem().toString()));
        requestLevel = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getLevel())
                        .get(levelAppCompatSpinner.getSelectedItem().toString()));
        requestMinVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMin.getText().toString());
        requestMaxVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMax.getText().toString());
        requestDescription = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                description.getText().toString().trim());
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
        if (photoUris.size() > 0) {
            images = new ArrayList<>();
            for (int i = 0; i < photoUris.size(); i++) {
                File image = new File(photoUris.get(i));
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY,
                        image.getName(), requestFile);
                images.add(part);
            }
        } else {
            images = null;
        }
        makeAddDiveSpotRequest();
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
        if (photos_rc.getVisibility() == View.VISIBLE) {
            photoUris.remove(event.getImageIndex());
            photos_rc.setAdapter(new AddPhotoToDsListAdapter(photoUris, AddDiveSpotActivity.this));
            return;
        }
        mapsUris.remove(event.getImageIndex());
        mapsRecyclerView.setAdapter(new AddPhotoToDsListAdapter(mapsUris, AddDiveSpotActivity.this));
    }

    private void showSuccessDialog(final String diveSpotId) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(this)
                .title(R.string.thank_you_title)
                .content(R.string.success_added)
                .positiveText(R.string.ok)
                .positiveColor(ContextCompat.getColor(this, R.color.primary))
                .cancelable(false)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        EventsTracker.trackCheckIn(EventsTracker.CheckInStatus.CANCELLED);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        if (!isFromMap) {
                            DiveSpotDetailsActivity.show(AddDiveSpotActivity.this, diveSpotId, null);
                            finish();
                        } else {
                            Intent intent = new Intent();
                            LatLng latLng = new LatLng(diveSpotLocation.latitude, diveSpotLocation.longitude);
                            intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_RESULT_LAT_LNG, latLng);
                            intent.putExtra(Constants.ADD_DIVE_SPOT_INTENT_DIVESPOT_ID, diveSpotId);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PERMISSION_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhotoFromGallery();
                } else {
                    Toast.makeText(AddDiveSpotActivity.this, "Grand permission to pick photo from gallery!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
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
        }
    }

    @Subscribe
    public void pickPhotoFrom(AddPhotoDoListEvent event) {
        pickPhotoFromGallery();
    }

}
