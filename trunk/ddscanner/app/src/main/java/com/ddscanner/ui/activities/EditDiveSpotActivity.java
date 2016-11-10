package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.EditDiveSpotEntity;
import com.ddscanner.entities.EditDiveSpotWrapper;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.DDScannerRestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.ui.dialogs.InfoDialogFragment;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogHelpers;
import com.ddscanner.utils.DialogsRequestCodes;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditDiveSpotActivity extends AppCompatActivity implements View.OnClickListener, InfoDialogFragment.DialogClosedListener {

    private static final String TAG = EditDiveSpotActivity.class.getSimpleName();

    private static final String DIVE_SPOT_NAME_PATTERN = "^[a-zA-Z0-9 ]*$";

    private int maxPhotosCount = 3;

    private String diveSpotId;

    private ImageButton btnAddPhoto;
    private ImageView btnAddSealife;

    private Toolbar toolbar;
    private LatLng diveSpotLocation;

    private LinearLayout pickLocation;
    private RecyclerView photos_rc;
    private TextView addPhotoTitle;
    private TextView locationTitle;
    private TextView addSealifeTitle;
    private Spinner levelSpinner;
    private Spinner currentsSpinner;
    private Spinner objectSpinner;
    private EditText name;
    private EditText depth;
    private EditText description;
    private Button btnSave;
    private RecyclerView sealifesRc;
    private ArrayList<String> deleted = new ArrayList<>();
    private SealifeListAddingDiveSpotAdapter sealifeListAddingDiveSpotAdapter;
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
    private EditText visibilityMin;
    private EditText visibilityMax;

    private List<String> imageUris = new ArrayList<>();
    private List<Sealife> sealifes = new ArrayList<>();
    private List<MultipartBody.Part> sealifeRequest = new ArrayList<>();
    private List<MultipartBody.Part> deletedImages = new ArrayList<>();
    private List<MultipartBody.Part> newImages = new ArrayList<>();
    private FiltersResponseEntity filters;
    private EditDiveSpotEntity diveSpot;
    private AddPhotoToDsListAdapter addPhotoToDsListAdapter;
    private ProgressDialog progressDialog;
    private Map<String, TextView> errorsMap = new HashMap<>();

    private RequestBody requestName, requestLat, requestLng, requestDepth,
            requestCurrents, requestLevel, requestObject,
            requestDescription, requestType, requestMinVisibility = null, requestMaxVisibility = null;
    private RequestBody requestSecret = null;
    private RequestBody requestSocial = null;
    private RequestBody requestToken = null;

    private DDScannerRestClient.ResultListener<EditDiveSpotWrapper> getDiveSpotForEditResultListener = new DDScannerRestClient.ResultListener<EditDiveSpotWrapper>() {
        @Override
        public void onSuccess(EditDiveSpotWrapper divespotDetails) {
            diveSpot = divespotDetails.getDivespot();
            sealifes = divespotDetails.getSealifes();
            if (diveSpot.getImages() != null) {
                imageUris = changeImageAddresses(diveSpot.getImages());
            }
            addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, EditDiveSpotActivity.this);
            diveSpotLocation = new LatLng(divespotDetails.getDivespot().getLat(),
                    divespotDetails.getDivespot().getLng());
            DDScannerApplication.getDdScannerRestClient().getFilters(getFiltersResultListener);
            setUi();
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            switch (errorType) {
                case UNAUTHORIZED_401:
                    SharedPreferenceHelper.logout();
                    LoginActivity.showForResult(EditDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA);
                    break;
                case DIVE_SPOT_NOT_FOUND_ERROR_C802:
                    // This is unexpected so track it
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_message_dive_spot_not_found, DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_DIVE_SPOT_NOT_FOUND, false);
                    break;
                case RIGHTS_NOT_FOUND_403:
                    // This is unexpected so track it
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_no_rights_to_edit_dive_spot, DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_NO_RIGHTS_ERROR, false);
                    break;
                default:
                    EventsTracker.trackUnknownServerError(url, errorMessage);
                    InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR, false);
                    break;
            }
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
                    SharedPreferenceHelper.logout();
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

    private DDScannerRestClient.ResultListener<FiltersResponseEntity> getFiltersResultListener = new DDScannerRestClient.ResultListener<FiltersResponseEntity>() {
        @Override
        public void onSuccess(FiltersResponseEntity result) {
            progressView.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            filters = result;
            setSpinnerValues(objectSpinner, filters.getObject(), diveSpot.getObject());
            setSpinnerValues(levelSpinner, filters.getLevel(), diveSpot.getLevel());
            setSpinnerValues(currentsSpinner, filters.getCurrents(), diveSpot.getCurrents());
        }

        @Override
        public void onConnectionFailure() {
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_connection_error_title, R.string.error_connection_failed, DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_FAILED_TO_CONNECT, false);
        }

        @Override
        public void onError(DDScannerRestClient.ErrorType errorType, Object errorData, String url, String errorMessage) {
            EventsTracker.trackUnknownServerError(url, errorMessage);
            InfoDialogFragment.showForActivityResult(getSupportFragmentManager(), R.string.error_server_error_title, R.string.error_unexpected_error, DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR, false);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dive_spot);
        diveSpotId = getIntent().getStringExtra(Constants.DIVESPOTID);
        findViews();
        toolbarSettings();
        DDScannerApplication.getDdScannerRestClient().getDiveSpotForEdit(diveSpotId, getDiveSpotForEditResultListener);
        makeErrorsMap();
        EventsTracker.trackDiveSpotEdit();
    }

    /**
     * Find views in current activity_add_dive_spot
     */
    private void findViews() {
        progressDialogUpload = Helpers.getMaterialDialog(this);
        progressDialog = new ProgressDialog(this);
        name = (EditText) findViewById(R.id.name);
        depth = (EditText) findViewById(R.id.depth);
        description = (EditText) findViewById(R.id.description);
        btnAddSealife = (ImageView) findViewById(R.id.btn_add_sealife);
        photos_rc = (RecyclerView) findViewById(R.id.photos_rc);
        btnAddPhoto = (ImageButton) findViewById(R.id.btn_add_photo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        levelSpinner = (Spinner) findViewById(R.id.level_spinner);
        objectSpinner = (Spinner) findViewById(R.id.object_spinner);
        currentsSpinner = (Spinner) findViewById(R.id.currents_spinner);
        pickLocation = (LinearLayout) findViewById(R.id.location_layout);
        locationTitle = (TextView) findViewById(R.id.location);
        btnSave = (Button) findViewById(R.id.button_create);
        sealifesRc = (RecyclerView) findViewById(R.id.sealifes_rc);
        addSealifeTitle = (TextView) findViewById(R.id.add_sealife_text);
        mainLayout = (ScrollView) findViewById(R.id.main_layout);
        progressView = (ProgressView) findViewById(R.id.progressBarFull);
        error_depth = (TextView) findViewById(R.id.error_depth);
        error_description = (TextView) findViewById(R.id.error_description);
        error_location = (TextView) findViewById(R.id.error_location);
        error_name = (TextView) findViewById(R.id.error_name);
        error_images = (TextView) findViewById(R.id.error_images);
        error_sealife = (TextView) findViewById(R.id.error_sealife);
        visibilityMax = (EditText) findViewById(R.id.maxVisibility);
        visibilityMin = (EditText) findViewById(R.id.minVisibility);
        error_visibility_max = (TextView) findViewById(R.id.error_visibility_max);
        error_visibility_min = (TextView) findViewById(R.id.error_visibility_min);
    }

    /**
     * Change data and settings of current activity views
     */
    private void setUi() {
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);

        btnSave.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);

        name.setText(diveSpot.getName());
        depth.setText(diveSpot.getDepth());
        description.setText(diveSpot.getDescription());
        locationTitle.setTextColor(ContextCompat.getColor(this, R.color.black_text));
        visibilityMax.setText(diveSpot.getVisibilityMax());
        visibilityMin.setText(diveSpot.getVisibilityMin());

        /* Recycler view with images settings*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);
        photos_rc.setAdapter(addPhotoToDsListAdapter);

        /* Recycler view with sealifes settings*/
        LinearLayoutManager sealifeLayoutManager = new LinearLayoutManager(
                EditDiveSpotActivity.this);
        sealifeLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sealifesRc.setNestedScrollingEnabled(false);
        sealifesRc.setHasFixedSize(false);
        sealifesRc.setLayoutManager(sealifeLayoutManager);
        sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter((ArrayList<Sealife>) sealifes,
                EditDiveSpotActivity.this, addSealifeTitle);
        sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);
        progressView.stop();
        progressView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Change title and adding back icon to toolbar
     */
    private void toolbarSettings() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_ac_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_dive_spot);
    }

    public static void show(Context context, String id) {
        Intent intent = new Intent(context, EditDiveSpotActivity.class);
        intent.putExtra("ID", id);
        context.startActivity(intent);
    }

    /**
     * Add path to images to name to have a full address of image for future work with this
     *
     * @param images
     * @return images
     */
    private List<String> changeImageAddresses(List<String> images) {
        for (int i = 0; i < images.size(); i++) {
            images.set(i, diveSpot.getDiveSpotPathMedium() + images.get(i));
        }
        return images;
    }

    private void pickPhotoFromGallery() {
        if (checkReadStoragePermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_PHOTO);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PERMISSION_READ_STORAGE);
        }
    }

    public boolean checkReadStoragePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_photo:
                pickPhotoFromGallery();
                break;
            case R.id.location_layout:
                Intent intent = new Intent(EditDiveSpotActivity.this,
                        PickLocationActivity.class);
                intent.putExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_LATLNG, diveSpotLocation);
                startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_LOCATION);
                break;
            case R.id.btn_add_sealife:
                Intent sealifeIntent = new Intent(EditDiveSpotActivity.this,
                        SearchSealifeActivity.class);
                startActivityForResult(sealifeIntent, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_SEALIFE);
                break;
            case R.id.button_create:
                createRequestBodyies();
                break;
        }
    }

    private ArrayList<String> removeAddressPart(ArrayList<String> deleted) {
        for (int i = 0; i < deleted.size(); i++) {
            deleted.set(i, deleted.get(i).replace(diveSpot.getDiveSpotPathMedium(), ""
            ));
        }
        return deleted;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DialogHelpers.showDialogAfterChanging(R.string.dialog_leave_title, R.string.dialog_leave_spot_message, this, this);
//                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_LOCATION:
                if (resultCode == RESULT_OK) {
                    this.diveSpotLocation = data.getParcelableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_LATLNG);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_PHOTO:
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

                                    imageUris.add(file.getPath());
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

                                imageUris.add(file.getPath());
                            } else {
                                Toast.makeText(this, "You can choose only images", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris, EditDiveSpotActivity.this));

                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_SEALIFE:
                Helpers.hideKeyboard(this);
                if (resultCode == RESULT_OK) {
                    Sealife sealife = (Sealife) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                    sealifeListAddingDiveSpotAdapter.add(sealife);
                    Log.i(TAG, sealifeListAddingDiveSpotAdapter.getSealifes().get(0).getName());
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND:
                if (resultCode == RESULT_OK) {
                    createAddDiveSpotRequest();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA:
                if (resultCode == RESULT_OK) {
                    DDScannerApplication.getDdScannerRestClient().getDiveSpotForEdit(diveSpotId, getDiveSpotForEditResultListener);
                }
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
                break;
        }
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
        progressDialogUpload.show();
        requestName = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                name.getText().toString());
        requestDepth = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                depth.getText().toString());
        requestLat = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                String.valueOf(diveSpotLocation.latitude));
        requestLng = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                String.valueOf(diveSpotLocation.longitude));
        requestObject = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getObject())
                        .get(objectSpinner.getSelectedItem().toString()));
        requestCurrents = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getCurrents())
                        .get(currentsSpinner.getSelectedItem().toString()));
        requestLevel = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getLevel())
                        .get(levelSpinner.getSelectedItem().toString()));
        requestMinVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMin.getText().toString());
        requestMaxVisibility = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), visibilityMax.getText().toString());

        if (SharedPreferenceHelper.isUserLoggedIn()) {
            requestSocial = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getSn());
            requestToken = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                    SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                requestSecret = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                        SharedPreferenceHelper.getSecret());
            }
        }
        requestDescription = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                description.getText().toString());
        requestType = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), "PUT");


        sealifeRequest = new ArrayList<>();
        for (int i = 0; i < sealifes.size(); i++) {
            sealifeRequest.add(MultipartBody.Part.createFormData("sealife[]", sealifes.get(i).getId()));
        }

        List<String> newFilesUrisList = addPhotoToDsListAdapter.getNewFilesUrisList();
        if (newFilesUrisList == null) {
            newImages = null;
        } else {
            for (String newImageUri : newFilesUrisList) {
                File image = new File(newImageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("images_new[]", image.getName(),
                        requestFile);
                newImages.add(part);
            }
        }

        if (deleted.size() > 0) {
            deleted = removeAddressPart(deleted);
            for (int i = 0; i < deleted.size(); i++) {
                deletedImages.add(MultipartBody.Part.createFormData("images_del[]", deleted.get(i)));
            }
        } else {
            deletedImages = null;
        }

        createAddDiveSpotRequest();

    }

    private void hideErrorsFields() {
        for (Map.Entry<String, TextView> entry : errorsMap.entrySet()) {
            entry.getValue().setVisibility(View.GONE);
        }
    }

    private void createAddDiveSpotRequest() {
        DDScannerApplication.getDdScannerRestClient().putUpdateDiveSpot(
                diveSpotId,
                sealifeRequest,
                newImages,
                deletedImages,
                updateDiveSpotResultListener,
                requestType,
                requestName,
                requestLat,
                requestLng,
                requestDepth,
                requestMinVisibility,
                requestMaxVisibility,
                requestCurrents,
                requestLevel,
                requestObject,
                requestDescription,
                requestToken,
                requestSocial,
                requestSecret
        );
    }

    private void setSpinnerValues(Spinner spinner, Map<String, String> values, String tag) {
        List<String> objects = new ArrayList<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
        }
        ArrayAdapter<String> adapter = new SpinnerItemsAdapter(this, R.layout.spinner_drop_down_item, objects);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(values.get(tag)));
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
        imageUris.remove(event.getImageIndex());
        if (addPhotoToDsListAdapter.getListOfDeletedImages() != null) {
            deleted.addAll(addPhotoToDsListAdapter.getListOfDeletedImages());
        }
        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, EditDiveSpotActivity.this);
        photos_rc.setAdapter(addPhotoToDsListAdapter);
        if (addPhotoToDsListAdapter.getNewFilesUrisList() != null) {
            maxPhotosCount = 3 - addPhotoToDsListAdapter.getNewFilesUrisList().size();
        }
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
                break;
            }

        }
    }

    @Override
    public void onDialogClosed(int requestCode) {
        switch (requestCode) {
            case DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_FAILED_TO_CONNECT:
            case DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_DIVE_SPOT_NOT_FOUND:
            case DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_NO_RIGHTS_ERROR:
            case DialogsRequestCodes.DRC_EDIT_DIVE_SPOT_ACTIVITY_UNEXPECTED_ERROR:
                finish();
                break;
        }
    }
}
