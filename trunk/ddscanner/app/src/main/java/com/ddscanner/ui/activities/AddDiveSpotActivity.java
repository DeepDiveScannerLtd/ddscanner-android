package com.ddscanner.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpot;
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
import com.ddscanner.rest.BaseCallbackOld;
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
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

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

public class AddDiveSpotActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddDiveSpotActivity.class.getSimpleName();
    private static final String DIVE_SPOT_NAME_PATTERN = "^[a-zA-Z0-9 ]*$";

    private ProgressDialog progressDialog;

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
    private EditText visibilityMin;
    private EditText visibilityMax;
    private EditText description;
    private Button btnSave;
    private RecyclerView sealifesRc;
    private SealifeListAddingDiveSpotAdapter sealifeListAddingDiveSpotAdapter = null;
    private AddPhotoToDsListAdapter addPhotoToDsListAdapter = null;
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

    private List<String> imageUris = new ArrayList<>();
    private List<Sealife> sealifes = new ArrayList<>();
    private Map<String, TextView> errorsMap = new HashMap<>();
    private FiltersResponseEntity filters;

    private RequestBody requestName = null, requestLat = null, requestLng = null,
            requestDepth = null, requestCurrents = null,
            requestLevel = null, requestObject = null,
            requestDescription = null, requestSocial = null, requestToken = null,
            requestSecret = null, requestMinVisibility = null, requestMaxVisibility = null;
    private List<MultipartBody.Part> sealife = new ArrayList<>();
    private List<MultipartBody.Part> images = new ArrayList<>();
    private boolean isFromMap;

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
        loadFiltersDataRequest();
        makeErrorsMap();
    }


    /**
     * Find views in current activity_add_dive_spot
     *
     * @author Andrei Lashkevich
     */

    private void findViews() {
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
        error_visibility_max = (TextView) findViewById(R.id.error_visibility_max);
        error_visibility_min = (TextView) findViewById(R.id.error_visibility_min);
        visibilityMax = (EditText) findViewById(R.id.maxVisibility);
        visibilityMin = (EditText) findViewById(R.id.minVisibility);
    }

    /**
     * Set UI settings for activity views
     *
     * @author Andrei Lashkevich
     */

    private void setUi() {
        progressDialogUpload = Helpers.getMaterialDialog(this);
        progressDialog = new ProgressDialog(this);
        btnSave.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        btnAddSealife.setOnClickListener(this);

        /* Recycler view with images settings*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddDiveSpotActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photos_rc.setNestedScrollingEnabled(false);
        photos_rc.setHasFixedSize(false);
        photos_rc.setLayoutManager(layoutManager);

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

        progressView.stop();
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
                if (resultCode == RESULT_OK) {
                    maxPhotos = maxPhotos - data.getStringArrayListExtra(MultiImageSelectorActivity
                            .EXTRA_RESULT).size();
                    imageUris.addAll(data.getStringArrayListExtra(MultiImageSelectorActivity
                            .EXTRA_RESULT));
                    photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris,
                            AddDiveSpotActivity.this, addPhotoTitle));
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_SEALIFE:
                Helpers.hideKeyboard(this);
                if (resultCode == RESULT_OK) {
                    Sealife sealife =(Sealife) data.getSerializableExtra(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);

                    if (Helpers.checkIsSealifeAlsoInList((ArrayList<Sealife>) sealifes, sealife.getId())) {
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.sealife_already_added);
                        return;
                    }
                    sealifes.add(sealife);
                    sealifeListAddingDiveSpotAdapter = new SealifeListAddingDiveSpotAdapter(
                            (ArrayList<Sealife>) sealifes, this, addSealifeTitle);
                    sealifesRc.setAdapter(sealifeListAddingDiveSpotAdapter);
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND:
                if (resultCode == RESULT_OK) {
                    createAddDiveSpotRequest();
                }
                break;
            case ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA:
                if (resultCode == RESULT_OK) {
                    loadFiltersDataRequest();
                } else {
                    finish();
                }
                break;
        }
    }

    private void setSpinnerValues(Spinner spinner, Map<String, String> values, String tag) {
        List<String> objects = new ArrayList<String>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            objects.add(entry.getValue());
            if (entry.getKey().equals(tag)) {

            }
        }
        ArrayAdapter<String> adapter = new SpinnerItemsAdapter(this, R.layout.spinner_item, objects);
        spinner.setAdapter(adapter);
    }

    private void loadFiltersDataRequest() {

        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new BaseCallbackOld() {
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
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        Helpers.errorHandling(AddDiveSpotActivity.this, errorsMap, responseString);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(AddDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_GET_DATA);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    }
                }
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

                    setSpinnerValues(objectSpinner, filters.getObject(), "");
                    setSpinnerValues(levelSpinner, filters.getLevel(), "");
                    setSpinnerValues(currentsSpinner, filters.getCurrents(), "");

                }
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(AddDiveSpotActivity.this);
            }
        });
    }

    private void createAddDiveSpotRequest() {
        progressDialogUpload.show();
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpot(
                requestName, requestLat, requestLng, requestDepth, requestMinVisibility, requestMaxVisibility,
                requestCurrents, requestLevel, requestObject, requestDescription,
                sealife, images, requestToken, requestSocial, requestSecret
                );
        call.enqueue(new BaseCallbackOld() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "success");
                progressDialogUpload.dismiss();
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
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (BadRequestException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (ValidationErrorException e) {
                        // TODO Handle
                        Helpers.errorHandling(AddDiveSpotActivity.this, errorsMap, responseString);
                    } catch (NotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UnknownErrorException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (DiveSpotNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    } catch (UserNotFoundException e) {
                        // TODO Handle
                        SharedPreferenceHelper.logout();
                        SocialNetworks.showForResult(AddDiveSpotActivity.this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN_TO_SEND);
                    } catch (CommentNotFoundException e) {
                        // TODO Handle
                        Helpers.showToast(AddDiveSpotActivity.this, R.string.toast_server_error);
                    }
                } else {
                    if (response.raw().code() == 200) {
                        String responseString = "";
                        try {
                            responseString = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseString);
                                responseString = jsonObject.getString(Constants.ADD_DIVE_SPOT_ACTIVITY_DIVESPOT);
                            } catch (JSONException e) {

                            }
                            EventsTracker.trackDivespotCreated();
                            DiveSpot diveSpot = new Gson().fromJson(responseString, DiveSpot.class);
                            showSuccessDialog(String.valueOf(diveSpot.getId()));
                           // finish();
                        } catch (IOException e) {

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                super.onFailure(call, t);
                progressDialogUpload.dismiss();
            }

            @Override
            public void onConnectionFailure() {
                DialogUtils.showConnectionErrorDialog(AddDiveSpotActivity.this);
            }
        });
    }

    private void pickPhotoFromGallery() {
        if (checkReadStoragePermission()) {
            MultiImageSelector.create(this)
                    .count(maxPhotos)
                    .start(this, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_PICK_PHOTO);
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
//                if (SharedPreferenceHelper.isUserLoggedIn()) {
                    createRequestBodyies();
//                } else {
//                    Intent loginIntent = new Intent(this, SocialNetworks.class);
//                    startActivityForResult(loginIntent, ActivitiesRequestCodes.REQUEST_CODE_ADD_DIVE_SPOT_ACTIVITY_LOGIN);
//                }
                break;
        }
    }

    private void createSocialDatarequests() {
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
                        .get(objectSpinner.getSelectedItem().toString()));
        requestCurrents = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getCurrents())
                        .get(currentsSpinner.getSelectedItem().toString()));
        requestLevel = RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT),
                Helpers.getMirrorOfHashMap(filters.getLevel())
                        .get(levelSpinner.getSelectedItem().toString()));
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
        if (imageUris.size() > 0) {
            images = new ArrayList<>();
            for (int i = 0; i < imageUris.size(); i++) {
                File image = new File(imageUris.get(i));
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY,
                        image.getName(), requestFile);
                images.add(part);
            }
        } else {
            images = null;
        }
        createAddDiveSpotRequest();
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
        imageUris.remove(event.getImageIndex());
        photos_rc.setAdapter(new AddPhotoToDsListAdapter(imageUris,
                AddDiveSpotActivity.this, addPhotoTitle));
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

}
