package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
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
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.events.ImageDeletedEvent;
import com.ddscanner.rest.BaseCallback;
import com.ddscanner.rest.ErrorsParser;
import com.ddscanner.rest.RestClient;
import com.ddscanner.ui.adapters.AddPhotoToDsListAdapter;
import com.ddscanner.ui.adapters.SealifeListAddingDiveSpotAdapter;
import com.ddscanner.ui.adapters.SpinnerItemsAdapter;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class EditDiveSpotActivity extends AppCompatActivity implements View.OnClickListener {

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

    private ArrayAdapter<String> objectAdapter;
    private ArrayAdapter<String> levelAdapter;
    private ArrayAdapter<String> accessAdapter;
    private ArrayAdapter<String> visibilityAdapter;
    private ArrayAdapter<String> currentsAdapter;


    private List<String> imageUris = new ArrayList<String>();
    private List<Sealife> sealifes = new ArrayList<>();
    private List<String> images_del = new ArrayList<>();
    private List<MultipartBody.Part> sealifeRequest = new ArrayList<>();
    private List<MultipartBody.Part> deletedImages = new ArrayList<>();
    private List<MultipartBody.Part> newImages = new ArrayList<>();
    private FiltersResponseEntity filters;
    private EditDiveSpotWrapper divespotDetails;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dive_spot);
        diveSpotId = getIntent().getStringExtra(Constants.DIVESPOTID);
        findViews();
        toolbarSettings();
        getDsInfoRequest();
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
        addPhotoTitle = (TextView) findViewById(R.id.add_photo_title);
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
        locationTitle.setTextColor(getResources().getColor(R.color.black_text));
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
     * Get dive spot information according last modification by current user
     */
    private void getDsInfoRequest() {

        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotForEdit(
                diveSpotId, Helpers.getUserQuryMapRequest());
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(EditDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } finally {
                        // This will be called only if response code is 200
                    }
                }
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    divespotDetails = new Gson().fromJson(responseString, EditDiveSpotWrapper.class);
                    diveSpot = divespotDetails.getDivespot();
                    sealifes = divespotDetails.getSealifes();
                    if (diveSpot.getImages() != null) {
                        imageUris = changeImageAddresses(diveSpot.getImages());
                    }
                    addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, EditDiveSpotActivity.this, addPhotoTitle);
                    diveSpotLocation = new LatLng(divespotDetails.getDivespot().getLat(),
                            divespotDetails.getDivespot().getLng());
                    loadFiltersDataRequest();
                    setUi();
                }

            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(EditDiveSpotActivity.this);
            }
        });
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
            MultiImageSelector.create(this)
                    .count(maxPhotosCount)
                    .start(this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_PHOTO);
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

    /**
     * Remove adress part from image URL for creating list of deleted images names
     *
     * @param deleted
     * @return
     */
    private ArrayList<String> removeAdressPart(ArrayList<String> deleted) {

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
                onBackPressed();
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
                if (resultCode == RESULT_OK) {
                    ArrayList<String> addedImages = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (addedImages != null) {
                        maxPhotosCount = maxPhotosCount - addedImages.size();
                        imageUris.addAll(addedImages);
                    }
                    addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris, EditDiveSpotActivity.this, addPhotoTitle);
                    photos_rc.setAdapter(addPhotoToDsListAdapter);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_PICK_SEALIFE:
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
                    getDsInfoRequest();
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
            deleted = removeAdressPart(deleted);
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
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateDiveSpot(
                diveSpotId,
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
                sealifeRequest,
                newImages,
                deletedImages,
                requestToken,
                requestSocial,
                requestSecret
        );
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i("response body is " + responseString);
                    try {
                        ErrorsParser.checkForError(response.code(), responseString);
                    } catch (ServerInternalErrorException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        Helpers.errorHandling(EditDiveSpotActivity.this, errorsMap, responseString);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(EditDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_EDIT_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(EditDiveSpotActivity.this, R.string.toast_server_error);
                    }
                }
                if (response.isSuccessful()) {
                    EventsTracker.trackDivespotEdited();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                progressDialogUpload.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                super.onFailure(call, t);
                progressDialogUpload.dismiss();
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(EditDiveSpotActivity.this);
            }
        });
    }

    private void loadFiltersDataRequest() {

        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new BaseCallback() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseString = "";
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    filters = new FiltersResponseEntity();
                    filters = new Gson().fromJson(responseString, FiltersResponseEntity.class);

                    Log.i(TAG, responseString);

                    setSpinnerValues(objectSpinner, filters.getObject(), diveSpot.getObject());
                    setSpinnerValues(levelSpinner, filters.getLevel(), diveSpot.getLevel());
                    setSpinnerValues(currentsSpinner, filters.getCurrents(), diveSpot.getCurrents());

                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(EditDiveSpotActivity.this);
            }

        });
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
            deleted.addAll((ArrayList<String>) addPhotoToDsListAdapter.getListOfDeletedImages());
        }
        addPhotoToDsListAdapter = new AddPhotoToDsListAdapter(imageUris,
                EditDiveSpotActivity.this, addPhotoTitle);
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
                return;
            }

        }
    }

}
